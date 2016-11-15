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
 * Created by joseph on 16/11/9.
 */
public class RoomHandler {

    //判断是否有房间可用
    public synchronized void joinGame(short type, UserEntity user) throws IOException {
        //遍历所有房间,找到相应等级有座位且未开始游戏的房间,进入房间
        Boolean isFindRoom=false;
        RoomEntity currRoom = null;
        for(int i = 0; i< roomList.size(); i++){
            currRoom= roomList.get(i);
            if(type == currRoom.getType() && currRoom.getPlayerNum()<currRoom.getMAXPLAYER() && currRoom.getState()==1){
                addPlayerToRoom(currRoom,user);
                isFindRoom=true;
                break;
            }
        }
        //否则创建新房间
        if(!isFindRoom)
            currRoom = createRoom(type,user);
    }

    //玩家进入房间
    public void addPlayerToRoom(RoomEntity roomEntity,UserEntity userEntity) throws IOException {
        final int index = roomList.indexOf(roomEntity);
        userEntity.setRoomIndex(index);
        //加入到广播列表
        roomEntity.addToUserList(userEntity.getId());
        //向该房间所有用户广播
        WebSocketForPad.broadcastMessage(roomEntity.getUserList(),userEntity.getUsername()+" join room success,room index:"+roomEntity.getIndex());
        //SIT OR AUDIENCE
        if(!sit(userEntity))//有座位则成为玩家,否则成为观众
            roomEntity.addAudience(userEntity);
        System.out.println("******"+userEntity.getUsername()+" join room success,room index:"+roomEntity.getIndex());
        //若房间有2个人且是新房间,则启动游戏进程,否则该房间游戏进程已开启等待下一局就行
        if(roomEntity.getPlayerNum()>1&&roomEntity.isNew()==true)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startGame(index);
                }
            }).start();

    }

    //创建房间
    public RoomEntity createRoom(short type,UserEntity userEntity) throws IOException {
        RoomEntity roomEntity = new RoomEntity(type);
        roomList.add(roomEntity);
        roomEntity.setIndex(roomList.indexOf(roomEntity));
        System.out.println("******"+userEntity.getUsername()+"  create room success,room index:"+roomEntity.getIndex());
        addPlayerToRoom(roomEntity,userEntity);
        return roomEntity;
    }

    //玩家坐下:若房间有空闲座位且游戏未开始,可以坐下
    public synchronized boolean sit(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room = roomList.get(roomIndex);
        if(room.getState()==1 && room.getPlayerNum()< room.getMAXPLAYER()){
            room.addPlayer(user);
            WebSocketForPad.broadcastMessage(room.getUserList(),user.getUsername()+" sit success");
            return true;
        }
        //sit失败则只通知自己
        WebSocketForPad.sendMessage(user.getId(),user.getUsername()+" sit fail");
        return false;
    }

    //玩家站起:离开座位,成为观众
    public synchronized void standUp(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room = roomList.get(roomIndex);
        for(SeatEntity item:room.getSeatEntities()){
            if(item.userEntity.getId()==user.getId()){
                room.deletePlayer(item);
                room.addAudience(user);
                WebSocketForPad.broadcastMessage(room.getUserList(),user.getUsername()+" standUp success");
                return;
            }
        }
        //standUp失败则只通知自己
        WebSocketForPad.sendMessage(user.getId(),user.getUsername()+" standUp fail");
    }

    //玩家离开:站起(可能是玩家可能是观众),离开房间
    public synchronized void leaveRoom(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room = roomList.get(roomIndex);
        standUp(user);//若是玩家先站起变成观众
        room.deleteAudience(user);
        room.deleteUserList(user.getId());
        WebSocketForPad.broadcastMessage(room.getUserList(),user.getUsername()+" leave success");
    }

    //开始游戏
    public void startGame(int index){
        //每局游戏开始前更新房间
        RoomEntity room= roomList.get(index);
        room.setState((short) 0);

        //***************游戏逻辑处理开始**************//
        //1.发送给房间里所有用户:游戏开始
        try {
            WebSocketForPad.broadcastMessage(room.getUserList(),"start Game!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.第一轮:每人发2张牌,通知大盲后一位玩家下注,直到所有玩家下注相等
        room.round=0;
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
        //从小盲开始下注
        int firstStake=room.getFirPlayer()<allSeat.size() ? room.getFirPlayer():allSeat.size();
        try {
            SeatEntity currSeat=allSeat.get(firstStake);
            currSeat.pay[0]=room.getType()*5;
            WebSocketForPad.broadcastMessage(room.getUserList(),currSeat.userEntity.getUsername()+"(小盲)下注:"+room.getType()*5);

            room.nextPlay();
            currSeat=allSeat.get(room.getFirPlayer());
            currSeat.pay[0]=room.getType()*10;
            WebSocketForPad.broadcastMessage(room.getUserList(),currSeat.userEntity.getUsername()+"(大盲)下注:"+room.getType()*10);

            room.nextPlay();
            currSeat=allSeat.get(room.getFirPlayer());
            WebSocketForPad.sendMessage(currSeat.userEntity.getId(),"请选择下注/弃牌/让牌");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //***************游戏逻辑处理结束**************//
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

    //下注
    public void stake(UserEntity user,int stake) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room=roomList.get(roomIndex);
        int round=room.round;
        //该次下注的玩家
        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());
        currSeat.pay[round]=currSeat.pay[round]+stake;
        WebSocketForPad.broadcastMessage(room.getUserList(),"第"+round+1+"轮下注, "+user.getUsername()+" 该次下注:"+stake+",本轮总下注:"+currSeat.pay[round]);
        //判断是否所有玩家该轮下注相等,若相等进入下一轮,否则继续下注
        room.nextPlay();
        SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());

        if(currSeat.pay[round]==nextSeat.pay[round]){
            round++;
            if(round==1)            //进入第2轮
                round2(room);
            else if(round==2)       //进入第3轮
                round3(room);
            else                    //进入第4轮
                round4(room);
        }
        else {//否则通知下一位玩家继续下注
            WebSocketForPad.sendMessage(nextSeat.userEntity.getId(),"第"+round+1+"轮继续下注,请选择下注/弃牌/让牌");
        }

    }

    //弃牌
    public void abandon(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room=roomList.get(roomIndex);
        //将该座位状态设置为弃牌, 以后跳过该座位
        SeatEntity currSeat=room.getSeatEntities().get(room.getFirPlayer());
        currSeat.state=2;
        //通知下一位玩家下注
        room.nextPlay();
        SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());
        WebSocketForPad.sendMessage(nextSeat.userEntity.getId(),"请选择下注/弃牌/让牌");
    }

    //让牌
    public void skip(UserEntity user) throws IOException {
//        int roomIndex=user.getRoomIndex();
//        //直接通知下一位玩家下注
//        RoomEntity room=roomList.get(roomIndex);
//        room.nextPlay();
//        SeatEntity nextSeat=room.getSeatEntities().get(room.getFirPlayer());
//        WebSocketForPad.sendMessage(nextSeat.userEntity.getId(),"请选择下注/弃牌/让牌");
         WebSocketForPad.sendMessage(user.getId(),"请选择下注/弃牌/让牌");
    }

    //第二轮:发3张公共牌,通知下一位玩家下注,直到所有玩家下注相等
    public void round2(RoomEntity room) throws IOException {
        room.round=1;
        room.publicCard[0]=getRandomCard(room.getCard());
        room.publicCard[1]=getRandomCard(room.getCard());
        room.publicCard[2]=getRandomCard(room.getCard());
        String msg="第1轮结束,下一轮公共牌:"+room.publicCard[0]+","+room.publicCard[1]+","+room.publicCard[2];
        WebSocketForPad.broadcastMessage(room.getUserList(),msg);
        WebSocketForPad.sendMessage(room.getSeatEntities().get(room.getFirPlayer()).userEntity.getId(),"请选择下注/弃牌/让牌");
    }


    //第三轮:发1张公共牌,通知下一位玩家下注,直到所有玩家下注相等
    public void round3(RoomEntity room) throws IOException {
        room.publicCard[3]=getRandomCard(room.getCard());
        String msg="第2轮结束,下一轮公共牌:"+room.publicCard[3];
        WebSocketForPad.broadcastMessage(room.getUserList(),msg);
        WebSocketForPad.sendMessage(room.getSeatEntities().get(room.getFirPlayer()).userEntity.getId(),"请选择下注/弃牌/让牌");
    }

    //第四轮:发1张公共牌,计算赢家,扣除积分,通知所有用户:游戏结束
    public void round4(RoomEntity room) throws IOException {
        room.publicCard[4]=getRandomCard(room.getCard());
        String msg="第3轮结束,下一轮公共牌:"+room.publicCard[4];
        WebSocketForPad.broadcastMessage(room.getUserList(),msg);
        calculate(room);
        //休眠5S给玩家选择是否离开,若玩家人数>2则开始下一局
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(room.getPlayerNum()>1)
            startGame(room.getIndex());
    }

    //计算赢家,扣除积分
    public void calculate(RoomEntity room) throws IOException {
        //TODO
        //花色数组,大小数组
        int[] num={0,0,0,0,0,0,0,0,0,0,0,0,0};
        int[] suit={0,0,0,0};
        int cardSuit;
        int cardNum;

        for(SeatEntity seat:room.getSeatEntities()){
            //1.遍历每个座位的手牌与5张公共牌,计算每个花色和每个大小各有多少
            for(Integer publicCard:room.getCard()){
                //每个花色的数量
                cardSuit=publicCard/14;
                suit[cardSuit]++;
                //每个牌大小的数量
                cardNum=publicCard%13==0?12:publicCard%13-1;
                num[cardNum]++;
            }
            //两张手牌
            cardSuit=seat.card1/14;
            suit[cardSuit]++;
            cardNum=seat.card1%13==0?12:seat.card1%13-1;
            num[cardNum]++;
            cardSuit=seat.card2/14;
            suit[cardSuit]++;
            cardNum=seat.card2%13==0?12:seat.card2%13-1;
            num[cardNum]++;

            //2.判断牌型大小,共9类:
            /*
            9同花顺：同一花色的顺子。（最大牌：K-Q-J-10-9 最小牌：A-2-3-4-5）
            8四条：四同张加单张。（最大牌：A-A-A-A-K 最小牌：2-2-2-2-3）
            7葫芦（豪斯）：三同张加对子。（最大牌：A-A-A-K-K 最小牌：2-2-2-3-3）
            6同花：同一花色。（最大牌：A-K-Q-J-9 最小牌：2-3-4-5-7）
            5顺子：花色不一样的顺子。（最大牌：A-K-Q-J-10 最小牌：A-2-3-4-5）
            4三条：三同张加两单张。（最大牌：A-A-A-K-Q 最小牌：2-2-2-3-4）
            3两对：（最大牌：A-A-K-K-Q 最小牌：2-2-3-3-4）
            2一对：（最大牌：A-A-K-Q-J 最小牌：2-2-3-4-5）
            1单牌：（最大牌：A-K-Q-J-9 最小牌：2-3-4-5-7
            */
            boolean isSuit=isSuit(suit);
            boolean isString=isString(num);
            boolean isFour=isFour(num);
            boolean isThree=isThree(num);
            boolean ispairs=ispairs(num);
            boolean isTwo=istwo(num);

            if(isSuit&&isString){//可能为同花顺
                int suitIndex=0;
                LinkedList<Integer> sameSuitNum=new LinkedList<>();
                //1.找到同花的花色
                for(int i=0;i<4;i++)
                    if(suit[i]>=5)
                        suitIndex=i;
                //2.把该花色的牌放一个数组里
                for(Integer publicCard:room.getCard()){
                    if(publicCard/14==suitIndex)
                        sameSuitNum.add(publicCard);
                }
                if(seat.card1/14==suitIndex)
                    sameSuitNum.add(seat.card1);
                if(seat.card2/14==suitIndex)
                    sameSuitNum.add(seat.card2);
                //3.判断同花的牌是否是顺子
                int[] arrayNum=new int[]{};
                for(int i=0;i<sameSuitNum.size();i++)
                    arrayNum[i]=sameSuitNum.get(i);
                if(isString(arrayNum)){
                    seat.cardType=9;
                    System.out.println("同花顺");
                }
                else{
                    seat.cardType=6;
                    System.out.println("同花");
                }
            }
            else if(isFour){//四条
                seat.cardType=8;
            }
            else if(isThree&&isTwo){//葫芦
                seat.cardType=7;
            }
            else if(isString){//顺子
                seat.cardType=5;
            }
            else if(isThree){//三条

            }
            else if(ispairs){//两对

            }
            else if(isTwo){//一对

            }
            else{//单牌

            }

        }
        WebSocketForPad.broadcastMessage(room.getUserList(),"游戏结束...您可以选择离开或开始下一局");
    }

    //是否同花
    boolean isSuit(int[] suit){
        for(int i=0;i<4;i++)
            if(suit[i]>=5)
                return true;

        return false;
    }

    //是否顺子
    boolean isString(int[] num){
        LinkedList<Integer> notNullNum=new LinkedList<>();
        for(int i=0;i<13;i++)
            if(num[i]!=0)
                notNullNum.add(i);
        //判断是否连续
        if(notNullNum.size()<5)
            return false;
        int count=0;
        int currNum=notNullNum.get(0);
        for(Integer item:notNullNum){
            if(currNum==item-1)
                count++;
            if(count>3)
                return true;
            if(currNum!=item-1)
                count=0;
            currNum=item;
        }

        return false;
    }

    //是否四条
    boolean isFour(int[] num){
        for(int i=0;i<13;i++)
            if(num[i]==4)
                return true;

        return false;
    }

    //是否三条
    boolean isThree(int[] num){
        for(int i=0;i<13;i++)
            if(num[i]==3)
                return true;

        return false;
    }

    //是否两对
    boolean ispairs(int[] num){
        int count=0;
        for(int i=0;i<13;i++)
            if(num[i]==4)
                count++;

        if(count>1)
            return true;

        return false;
    }

    //是否一对
    boolean istwo(int[] num){
        for(int i=0;i<13;i++)
            if(num[i]==2)
                return true;

        return false;
    }




}
