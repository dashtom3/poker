package com.server.ws.handler;

import com.server.ws.core.server.WebSocketForPad;
import com.server.ws.entity.RoomEntity;
import com.server.ws.entity.SeatEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by joseph on 16/11/16.
 */
//计算赢家,扣除积分
public class CalculaterHandler {
    public void calculate(RoomEntity room) throws IOException {
        //花色数组,大小数组
        int[] num={0,0,0,0,0,0,0,0,0,0,0,0,0};
        int[] suit={0,0,0,0};
        int cardSuit;
        int cardNum;

        //1.遍历每个座位的手牌与5张公共牌,计算每个玩家牌型(共10种)
        for(SeatEntity seat:room.getSeatEntities()){
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

            /* 判断牌型大小,共10类
            10皇家同花顺
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
            boolean isString=isString(num)==-1? false:true;
            seat.cardString="";

            if(isSuit&&isString){//可能为同花顺
                //同花的颜色索引,存放同花色的牌数组
                int suitIndex=0;
                int[] sameSuitNum={0,0,0,0,0,0,0,0,0,0,0,0,0};
                //1.找到同花的花色
                for(int i=0;i<4;i++)
                    if(suit[i]>=5)
                        suitIndex=i;
                //2.把该花色的牌放一个数组里
                for(Integer publicCard:room.getCard()){
                    if(publicCard/14==suitIndex){
                        cardNum=publicCard%13==0?12:publicCard%13-1;
                        sameSuitNum[cardNum]++;
                    }
                }
                if(seat.card1/14==suitIndex){
                    cardNum=seat.card1%13==0?12:seat.card1%13-1;
                    sameSuitNum[cardNum]++;
                }
                if(seat.card2/14==suitIndex){
                    cardNum=seat.card2%13==0?12:seat.card2%13-1;
                    sameSuitNum[cardNum]++;
                }
                //3.判断同花的牌是否是顺子
                int stringmun=isString(sameSuitNum);
                int count=0;
                if(stringmun==-1){
                    seat.cardType=6;//同花
                    for(int i=12;i>=0;i--){//继续比较同花,5张牌决定
                        if(sameSuitNum[i]!=0){
                            seat.geneCardString(i);
                            count++;
                        }
                        if(count==5)
                            break;
                    }
                }
                else if(stringmun==8)
                    seat.cardType=10;//皇家同花顺
                else{
                    seat.cardType=9;//同花顺
                    seat.geneCardString(stringmun);
                }
            }
            else if(isFour(num)>-1){//四条
                seat.cardType=8;
                int theFour=isFour(num);
                seat.geneCardString(theFour);
                for(int i=12;i>=0;i--) {//继续比较4条,2张牌决定
                    if(num[i]!=0&&i!=theFour){
                        seat.geneCardString(i);
                        break;
                    }
                }
            }
            else if(ispairsThree(num)>-1){//葫芦,避免KKKJJJ8这样是葫芦但错判为3条
                seat.cardType=7;
                int theThree=ispairsThree(num);
                seat.geneCardString(theThree);
                for(int i=theThree;i>=0;i--) {//继续找出另外一对
                    if(num[i]==3){
                        seat.geneCardString(i);
                        break;
                    }
                }
            }
            else if(isThree(num)>-1){//可能为葫芦或者3条
                int theThree=isThree(num);
                if(istwo(num)>-1){//葫芦,2张牌决定
                    seat.cardType=7;
                    seat.geneCardString(theThree);
                    seat.geneCardString(istwo(num));
                }
                else {//三条,3张牌决定
                    seat.cardType=4;
                    seat.geneCardString(theThree);
                    int count=0;
                    for(int i=12;i>=0;i--) {//继续比较剩下牌
                        if(num[i]!=0&&i!=theThree){
                            seat.geneCardString(i);
                            count++;
                        }
                        if(count==2)
                            break;
                    }
                }
            }
            else if(isString){//顺子,1张牌决定
                seat.cardType=5;
                seat.geneCardString(isString(num));
            }
            else if(ispairs(num)){//两对,3张牌决定
                seat.cardType=3;
                int count=0;
                for(int i=12;i>=0;i--) {//找出2对
                    if(num[i]==2){
                        seat.geneCardString(i);
                        count++;
                    }
                    if(count==2)
                        break;
                }
                for(int i=12;i>=0;i--) {//找出最大的单牌
                    if(num[i]==1)
                        seat.geneCardString(i);
                }
            }
            else if(istwo(num)>-1){//一对,4张牌决定
                seat.cardType=2;
                int theTwo=istwo(num);
                seat.geneCardString(theTwo);//找出一对
                int count=0;
                for(int i=12;i>=0;i--) {//找出剩下最大的3个单牌
                    if(num[i]==1){
                        seat.geneCardString(i);
                        count++;
                    }
                    if(count==3)
                        break;
                }
            }
            else{//单牌,5张牌决定
                seat.cardType=1;
                int count=0;
                for(int i=12;i>=0;i--) {//找出剩下最大的3个单牌
                    if(num[i]!=0){
                        seat.geneCardString(i);
                        count++;
                    }
                    if(count==5)
                        break;
                }
            }
            WebSocketForPad.broadcastMessage(room.getUserList(),seat.userEntity.getUsername()+"的牌型是:"+seat.cardType);
        }

        //2.根据牌型找出赢家,若最大牌型有2个以上玩家则继续判断📒
        int maxCardType=1;
        LinkedList<SeatEntity> candidateWinner = new LinkedList<>();
        List<SeatEntity> winnerList=new ArrayList<>();
        for(SeatEntity seat:room.getSeatEntities()){//找出最大牌型
            if(maxCardType<seat.cardType)
                maxCardType=seat.cardType;
        }
        for(SeatEntity seat:room.getSeatEntities()){//找出所有最大牌型的玩家
            if(maxCardType==seat.cardType)
                candidateWinner.add(seat);
        }
        if(candidateWinner.size()==1||maxCardType==10)//最大牌型玩家只有一个或皇家同花顺则直接获胜
            winnerList=candidateWinner;
        else{                                         //否则继续比较
            String maxCardString=candidateWinner.get(0).cardString;
            for(SeatEntity seat:candidateWinner){//找出最大牌
                if(seat.cardString.compareTo(maxCardString)>=0)
                    maxCardString=seat.cardString;
            }
            for(SeatEntity seat:candidateWinner){//找出所有最大牌的玩家
                if(maxCardString==seat.cardString)
                    winnerList.add(seat);
            }
        }

        //3.赢取积分
        int winStake=room.allStake/winnerList.size();//每个赢家赢的积分
        String winName="";
        //赢家赢取所有积分
        for(SeatEntity winner:winnerList){
            winner.score=winner.score+winStake;
            winName=winName+winner.userEntity.getUsername()+";";
            WebSocketForPad.sendMessage(winner.userEntity.getId(),"恭喜你获胜!您已赢取"+winStake+"积分");
        }
        WebSocketForPad.broadcastMessage(room.getUserList(),"玩家"+winName+"获胜");
    }

    //是否同花
    boolean isSuit(int[] suit){
        for(int i=0;i<4;i++)
            if(suit[i]>=5)
                return true;

        return false;
    }

    //是否顺子,返回-1不是,否则返回顺子的最小数字
    int isString(int[] num){
        int count=0;
        for(int i=12;i>=0;i--) {
            if(num[i]==0)
                count=0;
            else
                count++;

            if(count==5)
                return i;//返回8为皇家同花顺
        }
        return -1;
    }

    //是否四条
    int isFour(int[] num){
        for(int i=12;i>=0;i--)
            if(num[i]==4)
                return i;

        return -1;
    }

    //是否有2个3条,避免KKKJJJ8这样是葫芦但错判为3条
    int ispairsThree(int[] num){
        int count=0;
        for(int i=12;i>=0;i--){
            if(num[i]==3)
                count++;
            if(count>1)
                return i;
        }

        return -1;
    }

    //是否三条
    int isThree(int[] num){
        for(int i=12;i>=0;i--)
            if(num[i]==3)
                return i;

        return -1;
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
    int istwo(int[] num){
        for(int i=12;i>=0;i--)
            if(num[i]==2)
                return i;

        return -1;
    }

//    //牌型相同,继续比较
//    List<SeatEntity> calculaterAgain(int cardType,LinkedList<SeatEntity> seatEntities){
//        List<SeatEntity> winner=new ArrayList<>();
//        String maxCardString;
//        switch (cardType){
//            case 9://继续比较同花顺,1张牌决定大小
//                maxCardString="00";
//                for(SeatEntity seat:seatEntities){//找出最大牌
//                    if(seat.cardString.compareTo(maxCardString)>0)
//                        maxCardString=seat.cardString;
//                }
//                for(SeatEntity seat:seatEntities){//找出所有最大牌的玩家
//                    if(maxCardString==seat.cardString)
//                        winner.add(seat);
//                }
//                break;
//            case 8://继续比较4条,2张牌决定大小
//                maxCardString="0000";
//                for(SeatEntity seat:seatEntities){//找出最大牌
//                    if(seat.cardString.compareTo(maxCardString)>0)
//                        maxCardString=seat.cardString;
//                }
//                for(SeatEntity seat:seatEntities){//找出所有最大牌的玩家
//                    if(maxCardString==seat.cardString)
//                        winner.add(seat);
//                }
//                break;
//            case 7://继续比较葫芦,2张牌决定大小
//                maxCardString="0000";
//                for(SeatEntity seat:seatEntities){//找出最大牌
//                    if(seat.cardString.compareTo(maxCardString)>0)
//                        maxCardString=seat.cardString;
//                }
//                for(SeatEntity seat:seatEntities){//找出所有最大牌的玩家
//                    if(maxCardString==seat.cardString)
//                        winner.add(seat);
//                }
//                break;
//            case 6://继续比较同花,5张牌决定大小
//                maxCardString="0000000000";
//                for(SeatEntity seat:seatEntities){//找出最大牌
//                    if(seat.cardString.compareTo(maxCardString)>0)
//                        maxCardString=seat.cardString;
//                }
//                for(SeatEntity seat:seatEntities){//找出所有最大牌的玩家
//                    if(maxCardString==seat.cardString)
//                        winner.add(seat);
//                }
//                break;
//            case 5:
//
//                break;
//            case 4:
//
//                break;
//            case 3:
//
//                break;
//            case 2:
//
//                break;
//            case 1:
//                break;
//        }
//
//        return winner;
//    }

}
