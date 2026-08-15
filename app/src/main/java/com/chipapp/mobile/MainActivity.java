package com.chipapp.mobile;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private final int BLUE = Color.rgb(8,125,245), TEXT = Color.rgb(28,28,30), MUTED = Color.rgb(120,120,128), LINE = Color.rgb(229,229,234), BG = Color.rgb(242,242,247);
    private LinearLayout root, content, nav, messageList;
    private String screen = "chats", activeName = "Alya Putri", activeId = "alya";
    private String serverUrl;
    private EditText messageInput;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final String[][] contacts = {
        {"alya","Alya Putri","AP","Wah boleh banget! Jam berapa?","10.42","2","#EC7695"},
        {"produk","Tim Produk","TP","Bima: Mockup barunya keren 🔥","09.18","4","#5577D9"},
        {"raka","Raka Pradana","RP","Sip, sampai ketemu besok!","Kemarin","","#EBA94D"},
        {"mama","Mama","MA","Jangan lupa makan ya ❤️","Kemarin","","#45A98D"},
        {"nadia","Nadia S.","NS","📷 Foto","Senin","","#9B6BD5"},
        {"weekend","Weekend Escape","WE","Dito: Jadi berangkat pagi?","Minggu","","#D8734E"},
        {"bagas","Bagas","BG","Oke, noted. Makasih ya!","Sabtu","","#5C88AA"}
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        serverUrl = getPreferences(MODE_PRIVATE).getString("server", "http://127.0.0.1:3000");
        getWindow().setStatusBarColor(Color.rgb(248,248,250));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        showHome("chats");
    }

    private TextView text(String value, float sp, int color, boolean bold) {
        TextView v = new TextView(this); v.setText(value); v.setTextSize(sp); v.setTextColor(color);
        v.setGravity(Gravity.CENTER_VERTICAL); v.setFontFeatureSettings("kern");
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return v;
    }
    private GradientDrawable bg(int color, float radius) { GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private int dp(float n){ return (int)(n*getResources().getDisplayMetrics().density+.5f); }
    private LinearLayout row(){ LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l; }
    private Space space(int weight){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(0,1,weight));return s;}
    private TextView icon(String glyph){TextView v=text(glyph,21,BLUE,false);v.setGravity(Gravity.CENTER);v.setMinWidth(dp(42));v.setMinHeight(dp(42));v.setBackgroundColor(Color.TRANSPARENT);return v;}

    private void base() {
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.WHITE);
        setContentView(root);
    }
    private LinearLayout header(String left,String title,String right) {
        LinearLayout h=row();h.setPadding(dp(10),0,dp(10),0);h.setBackgroundColor(Color.rgb(248,248,250));
        h.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(54)));
        TextView l=text(left,14,BLUE,false);l.setGravity(Gravity.CENTER_VERTICAL);l.setPadding(dp(6),0,0,0);l.setLayoutParams(new LinearLayout.LayoutParams(dp(90),-1));h.addView(l);
        TextView t=text(title,17,TEXT,true);t.setGravity(Gravity.CENTER);t.setLayoutParams(new LinearLayout.LayoutParams(0,-1,1));h.addView(t);
        TextView r=text(right,22,BLUE,false);r.setGravity(Gravity.CENTER);r.setLayoutParams(new LinearLayout.LayoutParams(dp(90),-1));h.addView(r);
        return h;
    }

    private void showHome(String tab){
        screen="home";base();
        String title=tab.equals("chats")?"Chat":tab.equals("updates")?"Pembaruan":tab.equals("calls")?"Panggilan":tab.equals("groups")?"Komunitas":"Pengaturan";
        LinearLayout h=header(tab.equals("chats")?"Edit":"",title,tab.equals("chats")?"◉   ✎":tab.equals("updates")?"＋":"");root.addView(h);
        TextView right=(TextView)h.getChildAt(2); if(tab.equals("chats")) right.setOnClickListener(v->toast("Pilih kontak untuk chat baru"));
        FrameLayout frame=new FrameLayout(this);frame.setLayoutParams(new LinearLayout.LayoutParams(-1,0,1));root.addView(frame);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setBackgroundColor(tab.equals("settings")?BG:Color.WHITE);scroll.addView(content);frame.addView(scroll,new FrameLayout.LayoutParams(-1,-1));
        if(tab.equals("chats")) chats(); else if(tab.equals("updates")) updates(); else if(tab.equals("calls")) calls(); else if(tab.equals("groups")) communities(); else settings();
        bottomNav(tab);
    }

    private void searchBar(){
        TextView s=text("⌕   Cari",14,Color.rgb(142,142,147),false);s.setGravity(Gravity.CENTER);s.setBackground(bg(Color.rgb(232,232,236),10));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(36));p.setMargins(dp(16),dp(5),dp(16),dp(7));content.addView(s,p);s.setOnClickListener(v->toast("Pencarian dibuka"));
    }
    private void chats(){
        searchBar(); LinearLayout links=row();links.setPadding(dp(16),0,dp(16),0);links.setBackgroundColor(Color.WHITE);
        TextView broadcast=text("Daftar Siaran",13,BLUE,false), group=text("Grup Baru",13,BLUE,false);links.addView(broadcast);links.addView(space(1));links.addView(group);content.addView(links,new LinearLayout.LayoutParams(-1,dp(40)));
        LinearLayout filters=row();filters.setPadding(dp(16),dp(5),dp(16),dp(7));for(String f:new String[]{"Semua","Belum dibaca","Grup"}){TextView chip=text(f,11,f.equals("Semua")?BLUE:Color.DKGRAY,true);chip.setGravity(Gravity.CENTER);chip.setBackground(bg(f.equals("Semua")?Color.rgb(220,236,255):Color.rgb(239,239,242),16));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-2,dp(29));cp.setMargins(0,0,dp(7),0);cp.width=dp(f.equals("Belum dibaca")?92:58);filters.addView(chip,cp);}content.addView(filters);
        LinearLayout archived=row();archived.setPadding(dp(22),0,dp(16),0);archived.addView(text("▣",20,BLUE,false),new LinearLayout.LayoutParams(dp(55),-1));archived.addView(text("Diarsipkan",13,TEXT,false),new LinearLayout.LayoutParams(0,-1,1));archived.addView(text("0",11,MUTED,false));content.addView(archived,new LinearLayout.LayoutParams(-1,dp(48)));
        for(String[] c:contacts) addContact(c);
    }
    private void addContact(String[] c){
        LinearLayout item=row();item.setPadding(dp(16),dp(7),dp(14),0);item.setBackgroundColor(Color.WHITE);
        item.addView(avatar(c[2],c[6],50),new LinearLayout.LayoutParams(dp(50),dp(50)));
        LinearLayout copy=new LinearLayout(this);copy.setOrientation(LinearLayout.VERTICAL);copy.setPadding(dp(11),dp(3),0,dp(9));
        LinearLayout top=row();top.addView(text(c[1],14,TEXT,true),new LinearLayout.LayoutParams(0,dp(23),1));top.addView(text(c[4],10,c[5].isEmpty()?MUTED:BLUE,false));copy.addView(top);
        LinearLayout bottom=row();TextView prev=text(c[3],12,MUTED,false);prev.setSingleLine();bottom.addView(prev,new LinearLayout.LayoutParams(0,dp(23),1));if(!c[5].isEmpty()){TextView badge=text(c[5],9,Color.WHITE,true);badge.setGravity(Gravity.CENTER);badge.setBackground(bg(Color.rgb(52,199,89),20));bottom.addView(badge,new LinearLayout.LayoutParams(dp(19),dp(19)));}copy.addView(bottom);
        View line=new View(this);line.setBackgroundColor(LINE);copy.addView(line,new LinearLayout.LayoutParams(-1,1));item.addView(copy,new LinearLayout.LayoutParams(0,dp(66),1));content.addView(item,new LinearLayout.LayoutParams(-1,dp(66)));
        item.setOnClickListener(v->{activeId=c[0];activeName=c[1];showChat();});
    }
    private TextView avatar(String initials,String color,int size){TextView a=text(initials,size/4f,Color.WHITE,true);a.setGravity(Gravity.CENTER);a.setBackground(bg(Color.parseColor(color),size/2f));return a;}

    private void bottomNav(String active){
        nav=row();nav.setGravity(Gravity.CENTER);nav.setBackgroundColor(Color.rgb(248,248,250));String[][] tabs={{"updates","◉","Pembaruan"},{"calls","☎","Panggilan"},{"groups","♟","Komunitas"},{"chats","◌","Chat"},{"settings","⚙","Pengaturan"}};
        for(String[] t:tabs){LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.VERTICAL);b.setGravity(Gravity.CENTER);int col=t[0].equals(active)?BLUE:MUTED;TextView i=text(t[1],21,col,false);i.setGravity(Gravity.CENTER);TextView label=text(t[2],9,col,t[0].equals(active));label.setGravity(Gravity.CENTER);b.addView(i,new LinearLayout.LayoutParams(-1,dp(29)));b.addView(label,new LinearLayout.LayoutParams(-1,dp(18)));b.setOnClickListener(v->showHome(t[0]));nav.addView(b,new LinearLayout.LayoutParams(0,-1,1));}root.addView(nav,new LinearLayout.LayoutParams(-1,dp(66)));
    }

    private void updates(){
        section("Status");LinearLayout mine=row();mine.setPadding(dp(16),dp(8),dp(16),dp(8));mine.addView(avatar("VL","#5C9BEA",52),new LinearLayout.LayoutParams(dp(52),dp(52)));LinearLayout txt=new LinearLayout(this);txt.setOrientation(LinearLayout.VERTICAL);txt.setPadding(dp(12),0,0,0);txt.addView(text("Status Saya",14,TEXT,true));txt.addView(text("Tambah ke status saya",11,MUTED,false));mine.addView(txt,new LinearLayout.LayoutParams(0,-1,1));mine.addView(icon("◉"));mine.addView(icon("✎"));content.addView(mine);
        section("PEMBARUAN TERBARU");for(int i:new int[]{0,4,2,3}){String[] c=contacts[i];LinearLayout r=row();r.setPadding(dp(16),dp(7),0,dp(7));TextView a=avatar(c[2],c[6],50);GradientDrawable d=bg(Color.parseColor(c[6]),25);d.setStroke(dp(3),BLUE);a.setBackground(d);r.addView(a,new LinearLayout.LayoutParams(dp(50),dp(50)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.setPadding(dp(12),0,0,0);tx.addView(text(c[1],14,TEXT,true));tx.addView(text("Hari ini, "+c[4],11,MUTED,false));r.addView(tx,new LinearLayout.LayoutParams(0,-1,1));content.addView(r);}
        section("Saluran");for(String n:new String[]{"ChipApp News","Tech in 60s","Ruang Kreatif"}) simpleRow("◉",n,"2 pembaruan baru");
    }
    private void calls(){searchBar();LinearLayout link=row();link.setPadding(dp(16),dp(10),dp(16),dp(10));TextView i=icon("↗");i.setTextColor(Color.WHITE);i.setBackground(bg(BLUE,25));link.addView(i,new LinearLayout.LayoutParams(dp(48),dp(48)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.setPadding(dp(12),0,0,0);tx.addView(text("Buat Tautan Panggilan",14,TEXT,true));tx.addView(text("Bagikan tautan panggilan ChipApp",11,MUTED,false));link.addView(tx);content.addView(link);section("TERBARU");for(int x:new int[]{0,2,4,3})simpleRow(contacts[x][2],contacts[x][1],x==4?"↙ Panggilan tak terjawab":"↗ Kemarin, 20.04");}
    private void communities(){LinearLayout hero=new LinearLayout(this);hero.setOrientation(LinearLayout.VERTICAL);hero.setGravity(Gravity.CENTER);hero.setPadding(dp(30),dp(35),dp(30),dp(35));TextView ico=text("♟",40,BLUE,false);ico.setGravity(Gravity.CENTER);hero.addView(ico);TextView t=text("Komunitas ChipApp",21,TEXT,true);t.setGravity(Gravity.CENTER);hero.addView(t);TextView p=text("Satukan grup yang saling terhubung dan kirim pengumuman dengan mudah.",13,MUTED,false);p.setGravity(Gravity.CENTER);p.setPadding(0,dp(8),0,dp(20));hero.addView(p);TextView btn=text("Buat Komunitas",14,Color.WHITE,true);btn.setGravity(Gravity.CENTER);btn.setBackground(bg(BLUE,10));hero.addView(btn,new LinearLayout.LayoutParams(-1,dp(44)));content.addView(hero);section("KOMUNITAS ANDA");simpleRow("KL","Kreatif Lokal","3 grup • 128 anggota");}
    private void settings(){
        LinearLayout account=row();account.setPadding(dp(16),dp(12),dp(16),dp(12));account.setBackgroundColor(Color.WHITE);account.addView(avatar("VL","#5C9BEA",60),new LinearLayout.LayoutParams(dp(60),dp(60)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.setPadding(dp(12),0,0,0);tx.addView(text("Val",20,TEXT,true));tx.addView(text("Hidup adalah cerita ✨",12,MUTED,false));account.addView(tx,new LinearLayout.LayoutParams(0,-1,1));account.addView(text("›",25,MUTED,false));content.addView(account);
        addGap();settingRow("★","Pesan Berbintang",null);settingRow("⌘","Perangkat Tertaut",null);addGap();settingRow("⚙","Akun",null);settingRow("▣","Privasi",null);settingRow("◌","Chat",null);settingRow("♬","Notifikasi",null);settingRow("⇄","Penyimpanan dan Data",null);addGap();settingRow("◎","Alamat Server Termux",v->serverDialog());
        TextView version=text("ChipApp Native 1.0.0\nServer: "+serverUrl,10,MUTED,false);version.setGravity(Gravity.CENTER);version.setPadding(0,dp(22),0,dp(22));content.addView(version);
    }
    private void settingRow(String ico,String name,View.OnClickListener click){LinearLayout r=row();r.setPadding(dp(16),0,dp(16),0);r.setBackgroundColor(Color.WHITE);TextView i=text(ico,18,Color.WHITE,true);i.setGravity(Gravity.CENTER);i.setBackground(bg(BLUE,7));r.addView(i,new LinearLayout.LayoutParams(dp(29),dp(29)));TextView n=text(name,14,TEXT,false);n.setPadding(dp(12),0,0,0);r.addView(n,new LinearLayout.LayoutParams(0,dp(47),1));r.addView(text("›",22,MUTED,false));content.addView(r,new LinearLayout.LayoutParams(-1,dp(47)));if(click!=null)r.setOnClickListener(click);}
    private void addGap(){Space s=new Space(this);content.addView(s,new LinearLayout.LayoutParams(-1,dp(28)));}
    private void section(String name){TextView s=text(name,11,MUTED,true);s.setPadding(dp(16),dp(16),dp(16),dp(7));content.addView(s);}
    private void simpleRow(String initials,String title,String sub){LinearLayout r=row();r.setPadding(dp(16),dp(7),dp(16),dp(7));r.setBackgroundColor(Color.WHITE);r.addView(avatar(initials,"#5C9BEA",46),new LinearLayout.LayoutParams(dp(46),dp(46)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.setPadding(dp(11),0,0,0);tx.addView(text(title,14,TEXT,true));tx.addView(text(sub,11,MUTED,false));r.addView(tx,new LinearLayout.LayoutParams(0,-1,1));r.addView(text("›",22,MUTED,false));content.addView(r);}

    private void showChat(){
        screen="chat";base();LinearLayout h=row();h.setPadding(dp(4),0,dp(4),0);h.setBackgroundColor(Color.rgb(248,248,250));TextView back=icon("‹");h.addView(back,new LinearLayout.LayoutParams(dp(40),-1));back.setOnClickListener(v->showHome("chats"));h.addView(avatar(activeName.substring(0,1),"#EC7695",36),new LinearLayout.LayoutParams(dp(36),dp(36)));LinearLayout who=new LinearLayout(this);who.setOrientation(LinearLayout.VERTICAL);who.setPadding(dp(8),0,0,0);who.addView(text(activeName,13,TEXT,true));who.addView(text("online",9,MUTED,false));h.addView(who,new LinearLayout.LayoutParams(0,-1,1));TextView video=icon("▻"),phone=icon("☎");h.addView(video);h.addView(phone);root.addView(h,new LinearLayout.LayoutParams(-1,dp(55)));
        ScrollView scroll=new ScrollView(this);scroll.setBackgroundColor(Color.rgb(239,234,226));messageList=new LinearLayout(this);messageList.setOrientation(LinearLayout.VERTICAL);messageList.setPadding(dp(10),dp(12),dp(10),dp(12));scroll.addView(messageList);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        TextView lock=text("🔒 Pesan terenkripsi end-to-end",9,Color.rgb(120,108,75),false);lock.setGravity(Gravity.CENTER);lock.setBackground(bg(Color.rgb(255,246,204),9));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,dp(30));lp.gravity=Gravity.CENTER;messageList.addView(lock,lp);
        addBubble("Hai! Gimana harimu?","10.31",false);addBubble("Lumayan seru, baru selesai meeting panjang 😅","10.33",true);addBubble("Mau ngopi sore ini?","10.36",false);loadMessages();
        LinearLayout bar=row();bar.setPadding(dp(7),dp(6),dp(7),dp(6));bar.setBackgroundColor(Color.rgb(247,247,249));messageInput=new EditText(this);messageInput.setHint("Pesan");messageInput.setTextSize(13);messageInput.setSingleLine();messageInput.setPadding(dp(13),0,dp(10),0);messageInput.setBackground(bg(Color.WHITE,20));bar.addView(messageInput,new LinearLayout.LayoutParams(0,dp(42),1));TextView send=icon("➤");send.setTextColor(Color.WHITE);send.setBackground(bg(BLUE,21));LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(dp(42),dp(42));sp.setMargins(dp(6),0,0,0);bar.addView(send,sp);send.setOnClickListener(v->sendMessage());root.addView(bar,new LinearLayout.LayoutParams(-1,dp(55)));
    }
    private void addBubble(String msg,String time,boolean mine){LinearLayout line=row();line.setGravity(mine?Gravity.RIGHT:Gravity.LEFT);TextView bubble=text(msg+"   "+time+(mine?"  ✓✓":""),12,TEXT,false);bubble.setPadding(dp(11),dp(8),dp(11),dp(8));bubble.setBackground(bg(mine?Color.rgb(217,253,211):Color.WHITE,13));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2);p.setMargins(mine?dp(55):0,dp(3),mine?0:dp(55),0);line.addView(bubble,p);messageList.addView(line,new LinearLayout.LayoutParams(-1,-2));}

    private void serverDialog(){final EditText input=new EditText(this);input.setText(serverUrl);input.setHint("http://192.168.1.5:3000");input.setSingleLine();new AlertDialog.Builder(this).setTitle("Server Termux").setMessage("Masukkan alamat IP ponsel yang menjalankan server.").setView(input).setNegativeButton("Batal",null).setPositiveButton("Simpan",(d,w)->{serverUrl=input.getText().toString().replaceAll("/$","");getPreferences(MODE_PRIVATE).edit().putString("server",serverUrl).apply();toast("Alamat server disimpan");pingServer();}).show();}
    private void pingServer(){request("GET","/health",null,result->toast(result!=null?"Server Termux terhubung":"Server tidak dapat dijangkau"));}
    private void loadMessages(){request("GET","/messages?room="+activeId,null,result->{if(result==null)return;try{JSONArray a=new JSONArray(result);for(int i=0;i<a.length();i++){JSONObject m=a.getJSONObject(i);addBubble(m.getString("text"),m.optString("time",""),m.optString("sender").equals("me"));}}catch(Exception ignored){}});}
    private void sendMessage(){String value=messageInput.getText().toString().trim();if(value.isEmpty())return;String time=new SimpleDateFormat("HH.mm",Locale.getDefault()).format(new Date());addBubble(value,time,true);messageInput.setText("");try{JSONObject body=new JSONObject();body.put("room",activeId);body.put("sender","me");body.put("text",value);body.put("time",time);request("POST","/messages",body.toString(),r->{if(r==null)toast("Pesan tersimpan lokal; server offline");});}catch(Exception ignored){}}
    private interface Callback{void done(String result);}
    private void request(String method,String path,String body,Callback cb){new Thread(()->{String result=null;try{HttpURLConnection c=(HttpURLConnection)new URL(serverUrl+path).openConnection();c.setRequestMethod(method);c.setConnectTimeout(3000);c.setReadTimeout(3000);if(body!=null){c.setDoOutput(true);c.setRequestProperty("Content-Type","application/json");c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));}InputStream in=c.getResponseCode()<400?c.getInputStream():c.getErrorStream();result=new String(readAll(in),StandardCharsets.UTF_8);c.disconnect();}catch(Exception ignored){}String finalResult=result;handler.post(()->cb.done(finalResult));}).start();}
    private byte[] readAll(InputStream in)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)out.write(b,0,n);return out.toByteArray();}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    @Override public void onBackPressed(){if(screen.equals("chat"))showHome("chats");else super.onBackPressed();}
}
