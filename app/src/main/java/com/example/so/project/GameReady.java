package com.example.so.project;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class GameReady extends AppCompatActivity {
    TextView textView3; //채팅내용변수


    private TextView[] idTextView = new TextView[4];
    private TextView[] chatTextView = new TextView[4];
    private TextView[] readyTextView = new TextView[4];
    private EditText sendText;
    private String myID;
    private int myNumber;
    private Button btnGameReady;
    //private ConnectThread connectThread;
    private GameReadyMesgRecv recvThread;

    public Socket sock;
    //private String addr = "192.168.0.16".trim();
    //private int port = 8007;

    //private boolean isConnected = false;

    private MessageHandler mesgHandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ready);

        Intent intent = getIntent();
        myID = intent.getExtras().getString("id");

        Log.d("GGGGGGGGGGGG", myID);


        idTextView[0] = (TextView)findViewById(R.id.id0);
        idTextView[1] = (TextView)findViewById(R.id.id1);
        idTextView[2] = (TextView)findViewById(R.id.id2);
        idTextView[3] = (TextView)findViewById(R.id.id3);

        chatTextView[0] = (TextView)findViewById(R.id.chatTextView0);
        chatTextView[1] = (TextView)findViewById(R.id.chatTextView1);
        chatTextView[2] = (TextView)findViewById(R.id.chatTextView2);
        chatTextView[3] = (TextView)findViewById(R.id.chatTextView3);

        readyTextView[0] = (TextView) findViewById(R.id.readyTextView0);
        readyTextView[1] = (TextView) findViewById(R.id.readyTextView1);
        readyTextView[2] = (TextView) findViewById(R.id.readyTextView2);
        readyTextView[3] = (TextView) findViewById(R.id.readyTextView3);

        sendText = (EditText)findViewById(R.id.sendTextView);


        btnGameReady = (Button)findViewById(R.id.btn_GameReady);

        mesgHandler = new MessageHandler();



        initNetwork();


    }
/*
    public void enterclicked(View v) throws InterruptedException {

        final TextView textView3 = (TextView) findViewById(R.id.textView3);
        EditText editText4 = (EditText) findViewById(R.id.editText4);
        textView3.setVisibility(View.VISIBLE);
        textView3.setText(editText4.getText());
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                TimerTask task = new TimerTask(){
                    @Override
                    public void run(){
                        textView3.setVisibility(View.INVISIBLE);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task,3000);
            }
        });
        thread.start();
    }
*/
    public void onButtonGameReadyClicked(View v){
        btnGameReady.setText("준비완료");
        sendMesg("P2S_READY_GAME", myNumber+"");
    }

    public void onEnterClicked(View v){
        String mesg;
        mesg = sendText.getText().toString();

        sendMesg("P2S_SEND_GAME_READY_CHAT", myNumber+" "+mesg);
    }
    public Socket getSocket(){
        return sock;
    }

/*
    class ConnectThread extends Thread{
        String hostname;
        public ConnectThread(String addr){
            hostname=addr;
        }
        public void run(){
            try{

                sock = new Socket(hostname,port);

                SocketSingleton.setSocket(sock);

                isConnected = true;
//
//                OutputStream outstream = null;
//                outstream = sock.getOutputStream();
//
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outstream));
//
//
//                bw.write(myID);
//                bw.newLine();
//                bw.flush();

                Log.d("dffjjjjjjjjfffff","fffjjjjjjjjjjjjjjjjjjjjjjjff");

            } catch (UnknownHostException e) {
                Log.d("eeeeeeeeeeeeee","eeeeeeeeeeeee");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("xxxxxxxxxx","xxxxxxxxxxx");
                e.printStackTrace();
            }
        }
    }
*/
    private void sendMesg(String type, String data){
        GameMesgSender sendThread = new GameMesgSender(sock, type,data);
        sendThread.start();
    }

    private void initNetwork(){

//        connectThread = new ConnectThread(addr);
//        connectThread.start();
//
//        while(isConnected == false);

        sock = SocketSingleton.getSocket();

        recvThread = new GameReadyMesgRecv(this);
        recvThread.start();

        sendMesg("P2S_CONNECT_CLIENT", myID);

    }




    public static final int S2P_CLIENT_NUMBER = 100;
    public static final int S2P_START_GAME = 101;
    public static final int S2P_SEND_GAME_READY_CHAT = 102;



    class MessageHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){
                case S2P_CLIENT_NUMBER:
                    connectNewID(msg.arg1,(String)msg.obj);
                    break;
                case S2P_START_GAME:
                    startGame();
                    break;
                case S2P_SEND_GAME_READY_CHAT:
                    setMessage(msg.arg1, (String)msg.obj);
                    break;
            }
        }
    }
    public MessageHandler getHandler(){
        return mesgHandler;
    }





    private void startGame(){
        Intent intent = new Intent(getApplicationContext(),GamePlay.class);

        intent.putExtra("myNum", myNumber);
        intent.putExtra("myID",myID);


        intent.putExtra("player0",idTextView[0].getText().toString());
        intent.putExtra("player1",idTextView[1].getText().toString());
        intent.putExtra("player2",idTextView[2].getText().toString());                                //////////////////////////////////////////에러
        //intent.putExtra("player3",idTextView[3].getText().toString());



        startActivity(intent);
    }

    public void connectNewID(int num, String ID){
        if(ID.compareTo(myID) == 0)
            myNumber = num;

        idTextView[num].setText(ID);

        Log.d("GGGGGGGGGGGG", ID + num+"");


    }

    public void setMessage(int number, String mesg){
        chatTextView[number].setText(mesg);
    }

}