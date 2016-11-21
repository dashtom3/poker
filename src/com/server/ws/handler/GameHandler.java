package com.server.ws.handler;

import com.server.http.entity.UserEntity;
import com.server.ws.core.server.WebSocketForPad;
import com.server.ws.entity.RoomEntity;
import com.server.ws.entity.SeatEntity;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.server.ws.entity.GameEntity.roomList;

/**
 * Created by tian on 2016/10/19.
 */
public class GameHandler{

    //开始游戏
    public void startGame(int index){
        RoomEntity room= roomList.get(index);
        room.setState((short) 0);

        //***************游戏逻辑处理开始**************//
        //1.初始化:赌池清零,轮最大注清零,每人下注记录清零;广播现在每个人积分
        room.allStake=0;
        room.maxStake=0;
        for(SeatEntity seat:room.getSeatEntities()){
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
        //2.第一轮:每人发2张牌,通知大盲后一位玩家下注,直到所有玩家下注相等
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
        //从第一个玩家开始(第一次是小盲)下注
        int firstStake=room.getFirPlayer()<allSeat.size() ? room.getFirPlayer():0;
        room.bigBlind=firstStake+1<allSeat.size() ? firstStake+1:0;//注明大盲,第一局大盲必须下注
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

    //下注
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
        currSeat.pay[round]=currSeat.pay[round]+stake;//记录每位玩家每轮下注数
        room.allStake=room.allStake+stake;//筹码加入赌池
        room.maxStake=room.maxStake>currSeat.pay[round]?room.maxStake:currSeat.pay[round];//记录该轮最大注


        //2.得到需要进行操作的下一位玩家
        room.nextPlay();//得到下一位下注的玩家
        SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());
        boolean isnext=false;
        while (!isnext){//弃牌或allin则跳过该玩家,直到找到下一个正常玩家
            if(nextSeat.state==2||nextSeat.state==4){
                room.nextPlay();
                nextSeat=room.getSeatEntities().get(room.getFirPlayer());
                isnext=true;
            }
        }
        int minStake=room.maxStake-nextSeat.pay[round];//计算下一位玩家最小跟注


        //3.判断是通知下一位玩家下注还是进入下一轮
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
        //将该座位状态设置为弃牌, 以后跳过该座位
        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());
        currSeat.state=2;
        int abandonNum=0;
        for(SeatEntity seat:room.getSeatEntities())
                if(seat.state==2)
                    abandonNum++;
        if(abandonNum==room.getPlayerNum()-1){//只剩一个玩家,直接获胜
            for(SeatEntity seat:room.getSeatEntities())
                if(seat.state!=2){
                    seat.score=seat.score+room.allStake;//赢取积分
                    WebSocketForPad.sendMessage(seat.userEntity.getId(),"恭喜你获胜!您已赢取"+room.allStake+"积分");
                    WebSocketForPad.broadcastMessage(room.getUserList(),"玩家"+seat.userEntity.getUsername()+"获胜");
                }
        }
        else {//通知下一位玩家下注
            room.nextPlay();
            SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());
            WebSocketForPad.sendMessage(nextSeat.userEntity.getId(),"请选择下注/弃牌/让牌");
        }
    }

    //让牌
    public void skip(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room=roomList.get(roomIndex);
        //该玩家状态改为让牌
        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());
        currSeat.state=3;
        //若下一位玩家状态为让牌则进入下一轮,否则通知下一位玩家下注
        room.nextPlay();
        SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());
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
                endGame(room);
            }
        }
        else
            WebSocketForPad.sendMessage(nextSeat.userEntity.getId(),"请选择下注/弃牌/让牌");
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

    //亮牌,游戏结束
    public void endGame(RoomEntity room) throws IOException {
        //广播所有人2张底牌
        for(SeatEntity seat:room.getSeatEntities())
            WebSocketForPad.broadcastMessage(room.getUserList(),seat.userEntity.getUsername()+"玩家的底牌:"+seat.card1+","+seat.card2);
        //计算赢家扣除积分
        CalculaterHandler calculaterHandler=new CalculaterHandler();
        calculaterHandler.calculate(room);
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
