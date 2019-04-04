package com.example.so.project;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ReadyRoom extends AppCompatActivity {

    String myID;
    ListView roomListView;

    ArrayList<String> roomList;
    ArrayAdapter<String> listAdapter;

    private Socket sock;
    private boolean isConnected = false;
    private String addr = "192.168.0.16".trim();
    private int port = 8009;
    private ConnectThread connectThread;
    private ReadyRoomMesgRecv recvThread;
    private MessageHandler mesgHandler;

    private EditText roomText;

    Socket getSocket () {
        return sock;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_room);

        Intent intent = getIntent();
        myID = intent.getExtras().getString("id");
        TextView Idtext = (TextView) findViewById(R.id.IDtext);
        Idtext.setText(myID);
        roomList= new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,roomList);

        Log.d("dd", myID);
        roomText = (EditText)findViewById(R.id.roomText);
        Button enterbutton = (Button)findViewById(R.id.enterbutton);

        enterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GameReady.class);

                intent.putExtra("id",myID);


                startActivity(intent);
                Log.d("HHHHHHHHHHHHHHHH", myID);

            }
        });

        Button createButton = (Button)findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String roomname = roomText.getText().toString();
                    roomList.add(roomname);
                    listAdapter.notifyDataSetChanged();
            }
        });

        roomListView = (ListView)findViewById(R.id.roomListView);
        roomListView.setAdapter(listAdapter);
        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("gggggg",position+" "+id);
                String roomName = roomList.get(position);
                Log.d("Room Number : " , roomName);
            }
        });

        Button roomListRefresh = (Button)findViewById(R.id.roomListRefresh);
        roomListRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMesg("P2S_REQ_ROOM_LIST", "");
            }
        });

        mesgHandler = new MessageHandler();
        initNetwork ();
    }

    private void recvRoomList (String mesg) {
        String[] parsedStr;
        parsedStr = mesg.split(" ");

        int roomNum = Integer.parseInt(parsedStr[1]);

        for (int i = 0; i < roomNum; i++) {

            roomList.add(parsedStr[2+i]);
        }
        listAdapter.notifyDataSetChanged();



    }
    public static final int S2P_SEND_ROOM_LIST = 300;

    class MessageHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){

                case S2P_SEND_ROOM_LIST:
                    recvRoomList ((String)msg.obj);
                    break;
                    /*
                case S2P_START_GAME:
                    startGame();
                    break;
                case S2P_SEND_GAME_READY_CHAT:
                    setMessage(msg.arg1, (String)msg.obj);
                    break;*/
            }
        }
    }
    public MessageHandler getHandler(){
        return mesgHandler;
    }



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


            } catch (UnknownHostException e) {
                Log.d("eeeeeeeeeeeeee","eeeeeeeeeeeee");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("xxxxxxxxxx","xxxxxxxxxxx");
                e.printStackTrace();
            }
        }
    }

    private void sendMesg(String type, String data){
        GameMesgSender sendThread = new GameMesgSender(sock, type,data);
        sendThread.start();
    }

    private void initNetwork(){


        connectThread = new ConnectThread(addr);
        connectThread.start();

        while(isConnected == false);

        recvThread = new ReadyRoomMesgRecv(this);
        recvThread.start();

        //sendMesg("P2S_CONNECT_CLIENT", myID);

    }
}
