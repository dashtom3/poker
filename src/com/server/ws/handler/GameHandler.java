package com.server.ws.handler;

import com.server.http.entity.UserEntity;
import com.server.ws.core.server.WebSocketForPad;
import com.server.ws.entity.RoomEntity;
import com.server.ws.entity.SeatEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.server.ws.entity.GameEntity.roomList;

/**
 * Created by tian on 2016/10/19.
 */
public class GameHandler{
    private CalculaterHandler calculaterHandler=new CalculaterHandler();//牌型计算操作

    //开始游戏
    public void startGame(int index){
        RoomEntity room= roomList.get(index);
        room.setState((short) 0);

        //***************游戏逻辑处理开始**************//
        //1.初始化:赌池清零,每轮最大注清零,每人该局总下注记录与每轮下注记录清零;广播现在每个人积分
        room.allStake=0;
        room.maxStake=0;
        for(SeatEntity seat:room.getSeatEntities()){
            seat.stake=0;
            for(int i=0;i<4;i++)
                seat.pay[i]=0;
        }
        try {
            WebSocketForPad.broadcastMessage(room.getUserList(),"start Game!");
            for(SeatEntity seat:room.getSeatEntities())
                WebSocketForPad.broadcastMessage(room.getUserList(),seat.userEntity.getUsername()+"当前积分:"+seat.score);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.第一轮开始,每人发2张牌,
        room.round=1;
        LinkedList<SeatEntity> allSeat=room.getSeatEntities();
        for(int i=0;i<allSeat.size();i++){
            SeatEntity currSeat=allSeat.get(i);
            currSeat.card1 = getRandomCard(room.getCard());
            currSeat.card2 = getRandomCard(room.getCard());
            try {
                WebSocketForPad.sendMessage(currSeat.userEntity.getId(),"card1:"+currSeat.card1+"   card2:"+currSeat.card2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //3.小盲和大盲下注,并通知大盲后一位玩家下注
        int firstStake=room.getFirPlayer()<allSeat.size() ? room.getFirPlayer():0;
        room.bigBlind=firstStake+1<allSeat.size() ? firstStake+1:0;//注明大盲,第一局大盲必须操作
        try {
            SeatEntity currSeat=allSeat.get(firstStake);
            currSeat.pay[0]=room.getType();
            WebSocketForPad.broadcastMessage(room.getUserList(),currSeat.userEntity.getUsername()+"(小盲)下注:"+room.getType()*5);

            room.nextPlay();
            currSeat=allSeat.get(room.getFirPlayer());
            currSeat.pay[0]=room.getType()*2;
            WebSocketForPad.broadcastMessage(room.getUserList(),currSeat.userEntity.getUsername()+"(大盲)下注:"+room.getType()*10);

            room.nextPlay();
            currSeat=allSeat.get(room.getFirPlayer());
            WebSocketForPad.sendMessage(currSeat.userEntity.getId(),"请选择下注/弃牌/让牌");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //***************游戏逻辑处理结束**************//
    }

    //处理下注
    public void stake(UserEntity user,int stake) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room=roomList.get(roomIndex);
        int round=room.round;
        //1.处理该请求玩家的本次下注
        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());//该次下注的玩家
        if(currSeat.score==stake){//all-in
            currSeat.state=4;
            WebSocketForPad.broadcastMessage(room.getUserList(),"第"+round+"轮下注, "+user.getUsername()+"all in:"+stake+",本轮总下注:"+currSeat.pay[round]);

        }
        else {
            WebSocketForPad.broadcastMessage(room.getUserList(),"第"+round+"轮下注, "+user.getUsername()+" 该次下注:"+stake+",本轮总下注:"+currSeat.pay[round]);
        }
        currSeat.score=currSeat.score-stake;  //取出玩家筹码
        currSeat.stake=currSeat.stake+stake;  //统计本局游戏已出的积分
        currSeat.pay[round]=currSeat.pay[round]+stake;//记录每位玩家每轮下注数
        room.allStake=room.allStake+stake;//筹码加入赌池
        room.maxStake=room.maxStake>currSeat.pay[round]?room.maxStake:currSeat.pay[round];//记录该轮最大注

        //2.计算弃牌和allin的总人数
        int count=0;
        for(SeatEntity seat:room.getSeatEntities()){
            if(seat.state==2||seat.state==4)
                count++;
        }
        //3.若所有人allin则结束
        if(count==room.getPlayerNum()){
            for(SeatEntity seat:room.getSeatEntities())//广播所有人2张底牌
                WebSocketForPad.broadcastMessage(room.getUserList(),seat.userEntity.getUsername()+"玩家的底牌:"+seat.card1+","+seat.card2);
            calculaterHandler.calculate(room);//计算赢家扣除积分
            endGame(room);
        }
        //4.否则通知下一位玩家下注
        else {
            messageStake(roomIndex,round);
        }
    }

    //通知下注
    public void messageStake(int roomIndex,int round) throws IOException {
        RoomEntity room=roomList.get(roomIndex);
        //1.得到需要进行操作的下一位玩家
        room.nextPlay();//得到下一位下注的玩家
        SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());
        boolean isnext=false;
        while (!isnext){//弃牌或allin则跳过该玩家,直到找到下一个正常玩家
            if(nextSeat.state==2||nextSeat.state==4){
                room.nextPlay();
                nextSeat=room.getSeatEntities().get(room.getFirPlayer());
            }
            else {
                isnext=true;
            }
        }
        int minStake=room.maxStake-nextSeat.pay[round];//计算下一位玩家最小跟注


        //2.判断是通知下一位玩家下注还是进入下一轮
        if(room.maxStake==nextSeat.pay[round]){//若所有玩家该轮下注相等则进入下一轮,否则继续下注
            if(round==1&&room.getFirPlayer()==room.bigBlind){//第一轮大盲必须下注
                WebSocketForPad.sendMessage(nextSeat.userEntity.getId(),"第"+round+"轮继续下注,最小跟注:"+minStake+",请选择下注/弃牌/让牌");
            }
            else {
                round++;
                room.maxStake=0;//轮最大注清零
                if(round==2)            //进入第2轮
                    round2(room);
                else if(round==3)       //进入第3轮
                    round3(room);
                else if(round==4)       //进入第4轮
                    round4(room);
                else {                  //亮牌,游戏结束
                    for(SeatEntity seat:room.getSeatEntities())//广播所有人2张底牌
                        WebSocketForPad.broadcastMessage(room.getUserList(),seat.userEntity.getUsername()+"玩家的底牌:"+seat.card1+","+seat.card2);
                    calculaterHandler.calculate(room);//计算赢家扣除积分
                    endGame(room);
                }
            }
        }
        else {//否则通知下一位玩家继续下注
            WebSocketForPad.sendMessage(nextSeat.userEntity.getId(),"第"+round+1+"轮继续下注,最小跟注:"+minStake+",请选择下注/弃牌/让牌");
        }
    }

    //弃牌
    public void abandon(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room=roomList.get(roomIndex);
        //1.将该座位状态设置为弃牌, 以后跳过该座位
        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());
        currSeat.state=2;
        //2.计算已弃牌人数
        int abandonNum=0;
        for(SeatEntity seat:room.getSeatEntities())
                if(seat.state==2)
                    abandonNum++;
        //3.只剩一个玩家,直接获胜
        if(abandonNum==room.getPlayerNum()-1){
            List<SeatEntity> winnerList=new ArrayList<>();
            for(SeatEntity seat:room.getSeatEntities()){//找到赢家,计算积分
                if(seat.state!=2){
                    winnerList.add(seat);
                    break;
                }
            }
            calculaterHandler.calculateScore(room,winnerList);
            endGame(room);
        }
        //4.否则通知下一位玩家下注
        else {
            messageStake(roomIndex,room.round);
        }
    }

    //让牌
    public void skip(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room=roomList.get(roomIndex);
        //1.该玩家状态改为让牌
        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());
        currSeat.state=3;
        //2.找出下一位正常玩家(非弃牌\allin)
        room.nextPlay();
        SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());
        boolean isnext=false;
        while (!isnext){//弃牌或allin则跳过该玩家,直到找到下一个正常玩家
            if(nextSeat.state==2||nextSeat.state==4){
                room.nextPlay();
                nextSeat=room.getSeatEntities().get(room.getFirPlayer());
            }
            else {
                isnext=true;
            }
        }
        //3.若该玩家状态为让牌则进入下一轮,否则通知下一位玩家下注
        if(nextSeat.state==3){//所有人该轮让牌,进入下一轮
            int round=room.round;
            round++;
            if(round==2)            //进入第2轮
                round2(room);
            else if(round==3)       //进入第3轮
                round3(room);
            else if(round==4)       //进入第4轮
                round4(room);
            else {                  //亮牌,游戏结束
                for(SeatEntity seat:room.getSeatEntities())//广播所有人2张底牌
                    WebSocketForPad.broadcastMessage(room.getUserList(),seat.userEntity.getUsername()+"玩家的底牌:"+seat.card1+","+seat.card2);
                calculaterHandler.calculate(room);//计算赢家扣除积分
                endGame(room);
            }
        }
        else {//通知下一位玩家下注
            messageStake(roomIndex,room.round);
        }
    }

//    //ALL-IN
//    public void allin(UserEntity user,int stake) throws IOException {
//        int roomIndex=user.getRoomIndex();
//        RoomEntity room=roomList.get(roomIndex);
//        int round=room.round;
//        //1.该玩家游戏状态改为allin,下注时候跳过
//        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());
//
//        //2.扣除筹码,通知
//        currSeat.score=0;  //取出玩家筹码
//        currSeat.pay[round]=currSeat.pay[round]+stake;//记录每位玩家每轮下注数
//        room.allStake=room.allStake+stake;//筹码加入赌池
//        WebSocketForPad.broadcastMessage(room.getUserList(),"第"+round+"轮下注, "+user.getUsername()+" 该次下注:"+stake+",本轮总下注:"+currSeat.pay[round]);
//        //记录该轮最大注
//        room.maxStake=room.maxStake>currSeat.pay[round]?room.maxStake:currSeat.pay[round];
//    }

    //第二轮:发3张公共牌,通知下一位玩家下注,直到所有玩家下注相等
    public void round2(RoomEntity room) throws IOException {
        room.round=2;
        room.publicCard[0]=getRandomCard(room.getCard());
        room.publicCard[1]=getRandomCard(room.getCard());
        room.publicCard[2]=getRandomCard(room.getCard());
        String msg="第1轮结束,第2轮公共牌:"+room.publicCard[0]+","+room.publicCard[1]+","+room.publicCard[2];
        WebSocketForPad.broadcastMessage(room.getUserList(),msg);
        WebSocketForPad.sendMessage(room.getSeatEntities().get(room.getFirPlayer()).userEntity.getId(),"请选择下注/弃牌/让牌");
    }


    //第三轮:发1张公共牌,通知下一位玩家下注,直到所有玩家下注相等
    public void round3(RoomEntity room) throws IOException {
        room.round=3;
        room.publicCard[3]=getRandomCard(room.getCard());
        String msg="第2轮结束,第3轮轮公共牌:"+room.publicCard[3];
        WebSocketForPad.broadcastMessage(room.getUserList(),msg);
        WebSocketForPad.sendMessage(room.getSeatEntities().get(room.getFirPlayer()).userEntity.getId(),"请选择下注/弃牌/让牌");
    }

    //第四轮:发1张公共牌,通知下一位玩家下注,直到所有玩家下注相等
    public void round4(RoomEntity room) throws IOException {
        room.round=4;
        room.publicCard[4]=getRandomCard(room.getCard());
        String msg="第3轮结束,第4轮公共牌:"+room.publicCard[4];
        WebSocketForPad.broadcastMessage(room.getUserList(),msg);
        WebSocketForPad.sendMessage(room.getSeatEntities().get(room.getFirPlayer()).userEntity.getId(),"请选择下注/弃牌/让牌");
    }

    //游戏结束
    public void endGame(RoomEntity room) throws IOException {
        //游戏状态设为结束,允许玩家进行房间操作
        room.setState((short) 1);
        WebSocketForPad.broadcastMessage(room.getUserList(),"游戏结束,您可以等待5秒开始下一局,或者离开");
        //休眠5S给玩家选择是否离开,若玩家人数>2则开始下一局
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(room.getPlayerNum()>1)
            startGame(room.getIndex());
    }

    //生成一张牌
    public int getRandomCard(List<Integer> currCard){
        while (true){
            Random r = new Random();
            int temp = r.nextInt(54);
            if(currCard.contains(temp)){
                getRandomCard(currCard);
            }else{
                currCard.add(temp);
                return temp;
            }
        }
    }

}
