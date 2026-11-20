package com.gathin.cleanyourtab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** DotDeck: launchable app, HOME candidate, local widget host; does not modify Xiaomi Home. */
public class MainActivity extends Activity {
    private static final int BG=Color.rgb(8,11,16), WHITE=Color.rgb(240,244,248), MUTED=Color.rgb(160,170,182);
    private static final int ACCENT=Color.rgb(255,87,92), PANEL=Color.argb(219,24,30,39);
    private static final int REQ_BIND=41, REQ_CONFIG=42, REQ_HOME=43;
    private final Handler clockHandler=new Handler(Looper.getMainLooper());
    private final List<AppItem> apps=new ArrayList<>();
    private final List<String> dockIds=new ArrayList<>();
    private SharedPreferences prefs;
    private AppWidgetHost widgetHost;
    private AppWidgetManager widgetManager;
    private FrameLayout widgetSlot;
    private LinearLayout dockRow;
    private GridLayout appGrid;
    private TextView clockText, dateText, batteryText, appCount;
    private EditText appSearch;
    private int pendingWidgetId=-1;
    private boolean widgetListening=false;
    private final Runnable tick=new Runnable(){public void run(){updateClock();clockHandler.postDelayed(this,15000);}};

    private static class AppItem {
        final String label, id;
        final ComponentName component;
        final Drawable icon;
        AppItem(String label, ComponentName component,Drawable icon){this.label=label;this.component=component;this.id=component.flattenToString();this.icon=icon;}
    }

    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        prefs=getSharedPreferences("cleanyourtab",MODE_PRIVATE);
        widgetManager=AppWidgetManager.getInstance(this);
        widgetHost=new AppWidgetHost(this,72081);
        loadApps(); loadDock(); build();
    }
    @Override protected void onStart(){super.onStart();try{widgetHost.startListening();widgetListening=true;}catch(Exception ignored){} clockHandler.post(tick);}
    @Override protected void onStop(){clockHandler.removeCallbacks(tick);if(widgetListening){widgetHost.stopListening();widgetListening=false;} super.onStop();}
    @Override public void onConfigurationChanged(Configuration cfg){super.onConfigurationChanged(cfg);build();}
    @Override public void onResume(){super.onResume();loadApps();if(appGrid!=null)fillApps(appSearch==null?"":appSearch.getText().toString()); if(dockRow!=null)refreshDock();updateClock();}

    private void loadApps(){
        apps.clear(); PackageManager pm=getPackageManager();
        Intent query=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> results=pm.queryIntentActivities(query,0);
        for(ResolveInfo ri:results){
            if(ri.activityInfo==null||getPackageName().equals(ri.activityInfo.packageName))continue;
            ComponentName c=new ComponentName(ri.activityInfo.packageName,ri.activityInfo.name);
            try{apps.add(new AppItem(ri.loadLabel(pm).toString(),c,ri.loadIcon(pm)));}catch(Exception ignored){}
        }
        Collections.sort(apps,(a,b)->a.label.compareToIgnoreCase(b.label));
    }
    private void loadDock(){
        dockIds.clear();String raw=prefs.getString("dock","");
        if(!raw.isEmpty()) for(String id:raw.split("\\|")) if(!id.isEmpty()) dockIds.add(id);
        if(dockIds.isEmpty()){
            String[] preferred={"com.android.chrome","com.google.android.youtube","com.google.android.apps.photos","com.google.android.gm","com.android.settings"};
            for(String pkg:preferred) for(AppItem app:apps) if(app.component.getPackageName().equals(pkg)&&!dockIds.contains(app.id)){dockIds.add(app.id);break;}
            for(AppItem app:apps){if(dockIds.size()>=5)break;if(!dockIds.contains(app.id))dockIds.add(app.id);}
        }
    }
    private void persistDock(){prefs.edit().putString("dock",String.join("|",dockIds)).apply();}

    private void build(){
        boolean landscape=getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE;
        int outerPad=landscape?24:16;
        FrameLayout base=new FrameLayout(this);base.setBackgroundColor(BG);
        base.addView(new DotBackdrop(this),new FrameLayout.LayoutParams(-1,-1));
        LinearLayout screen=new LinearLayout(this);screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(outerPad),dp(12),dp(outerPad),dp(12));
        FrameLayout.LayoutParams screenParams=new FrameLayout.LayoutParams(-1,-1);
        base.addView(screen,screenParams);
        base.setOnApplyWindowInsetsListener((v,insets)->{
            android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars());
            screen.setPadding(dp(outerPad),dp(12)+bars.top,dp(outerPad),dp(12)+bars.bottom);
            return insets;
        });
        setContentView(base);

        LinearLayout header=row();header.setGravity(Gravity.CENTER_VERTICAL);
        screen.addView(header,new LinearLayout.LayoutParams(-1,dp(46)));
        TextView brand=label("C L E A N  /  Y O U R  /  T A B",12,WHITE,true);
        header.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
        TextView home=chip("SET HOME",ACCENT);home.setOnClickListener(v->requestHome());header.addView(home);
        TextView settings=chip("⚙",WHITE);settings.setOnClickListener(v->openSettings());
        LinearLayout.LayoutParams sx=new LinearLayout.LayoutParams(-2,-2);sx.leftMargin=dp(8);header.addView(settings,sx);

        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);scroll.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams slp=new LinearLayout.LayoutParams(-1,0,1);screen.addView(scroll,slp);
        LinearLayout sections=col();sections.setPadding(0,dp(12),0,dp(14));scroll.addView(sections);

        LinearLayout hero=landscape?row():col();
        sections.addView(hero,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout clockCard=col();clockCard.setPadding(dp(22),dp(18),dp(22),dp(18));clockCard.setBackground(cardShape());
        LinearLayout.LayoutParams ccp=landscape?new LinearLayout.LayoutParams(0,dp(210),1):new LinearLayout.LayoutParams(-1,dp(195));
        hero.addView(clockCard,ccp);
        TextView eyebrow=label("NOTHING TO DISTRACT. EVERYTHING TO DO.",10,MUTED,true);clockCard.addView(eyebrow);
        clockText=label("00:00",landscape?62:56,WHITE,true);clockText.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));clockCard.addView(clockText);
        dateText=label("",15,MUTED,false);clockCard.addView(dateText);
        TextView quote=label("Make space for what matters  •",12,ACCENT,false);LinearLayout.LayoutParams qp=new LinearLayout.LayoutParams(-1,-2);qp.topMargin=dp(8);clockCard.addView(quote,qp);

        LinearLayout right=col();right.setPadding(dp(18),dp(16),dp(18),dp(12));right.setBackground(cardShape());
        LinearLayout.LayoutParams rp=landscape?new LinearLayout.LayoutParams(0,dp(210),1):new LinearLayout.LayoutParams(-1,dp(160));
        if(landscape)rp.leftMargin=dp(12);else rp.topMargin=dp(12);hero.addView(right,rp);
        LinearLayout topline=row();topline.setGravity(Gravity.CENTER_VERTICAL);right.addView(topline);
        TextView widgetHead=label("WIDGET SPACE",12,WHITE,true);topline.addView(widgetHead,new LinearLayout.LayoutParams(0,-2,1));
        TextView add=chip("+ ADD",ACCENT);add.setOnClickListener(v->chooseWidget());topline.addView(add);
        widgetSlot=new FrameLayout(this);
        LinearLayout.LayoutParams wlp=new LinearLayout.LayoutParams(-1,0,1);wlp.topMargin=dp(10);right.addView(widgetSlot,wlp);
        renderWidget();

        LinearLayout status=row();status.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams stp=new LinearLayout.LayoutParams(-1,dp(52));stp.topMargin=dp(10);sections.addView(status,stp);
        batteryText=label("BATTERY · --",12,MUTED,true);status.addView(batteryText,new LinearLayout.LayoutParams(0,-2,1));
        appCount=label("",12,MUTED,true);status.addView(appCount);

        LinearLayout searchWrap=col();searchWrap.setPadding(dp(16),dp(1),dp(16),dp(1));searchWrap.setBackground(cardShape());
        sections.addView(searchWrap,new LinearLayout.LayoutParams(-1,dp(52)));
        appSearch=new EditText(this);appSearch.setSingleLine(true);appSearch.setTextColor(WHITE);appSearch.setHintTextColor(MUTED);appSearch.setTextSize(14);
        appSearch.setHint("⌕  Search installed apps");appSearch.setBackgroundColor(Color.TRANSPARENT);
        searchWrap.addView(appSearch,new LinearLayout.LayoutParams(-1,-1));
        appSearch.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int start,int before,int count){fillApps(s.toString());}public void afterTextChanged(Editable e){} });

        TextView appsHead=label("YOUR SPACE  /  APPS",12,WHITE,true);
        LinearLayout.LayoutParams ahp=new LinearLayout.LayoutParams(-1,-2);ahp.topMargin=dp(17);ahp.bottomMargin=dp(10);sections.addView(appsHead,ahp);
        appGrid=new GridLayout(this);appGrid.setColumnCount(landscape?8:5);appGrid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        sections.addView(appGrid,new LinearLayout.LayoutParams(-1,-2));fillApps("");

        LinearLayout dockCard=row();dockCard.setGravity(Gravity.CENTER);dockCard.setPadding(dp(10),dp(7),dp(10),dp(7));
        dockCard.setBackground(roundColor(Color.argb(244,36,43,53),30));
        LinearLayout.LayoutParams dp0=new LinearLayout.LayoutParams(-1,dp(88));dp0.topMargin=dp(8);screen.addView(dockCard,dp0);
        TextView dot=label("●",18,ACCENT,true);dot.setGravity(Gravity.CENTER);dockCard.addView(dot,new LinearLayout.LayoutParams(dp(34),-1));
        dockRow=row();dockRow.setGravity(Gravity.CENTER);dockCard.addView(dockRow,new LinearLayout.LayoutParams(0,-1,1));
        TextView edit=chip("EDIT",WHITE);edit.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Floating dock")
           .setMessage("Long-press any app to pin or unpin it from the dock (maximum 7 apps).")
           .setPositiveButton("OK",null).show());dockCard.addView(edit);
        refreshDock();updateClock();
    }
    private void fillApps(String filter){
        if(appGrid==null)return;appGrid.removeAllViews();String q=filter.trim().toLowerCase(Locale.ROOT);
        int count=0, cols=appGrid.getColumnCount();
        for(AppItem app:apps){if(!app.label.toLowerCase(Locale.ROOT).contains(q))continue;
            FrameLayout tile=appTile(app,false);
            GridLayout.LayoutParams gp=new GridLayout.LayoutParams(GridLayout.spec(count/cols),GridLayout.spec(count%cols,1f));
            gp.width=0;gp.height=dp(100);gp.setGravity(Gravity.FILL_HORIZONTAL);gp.setMargins(dp(2),dp(3),dp(2),dp(3));
            appGrid.addView(tile,gp);count++;
        }
        if(appCount!=null)appCount.setText(count+" APPS");
    }
    private FrameLayout appTile(AppItem app,boolean small){
        FrameLayout tile=new FrameLayout(this);tile.setBackground(roundColor(Color.argb(90,255,255,255),small?20:22));
        LinearLayout body=col();body.setGravity(Gravity.CENTER);body.setPadding(dp(2),dp(5),dp(2),dp(5));
        tile.addView(body,new FrameLayout.LayoutParams(-1,-1));
        ImageView image=new ImageView(this);image.setImageDrawable(app.icon);image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        body.addView(image,new LinearLayout.LayoutParams(dp(small?37:44),dp(small?37:44)));
        if(!small){TextView name=label(app.label,11,WHITE,false);name.setMaxLines(2);name.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(-1,-2);np.topMargin=dp(8);body.addView(name,np);}
        tile.setOnClickListener(v->{try{Intent i=new Intent(Intent.ACTION_MAIN);i.addCategory(Intent.CATEGORY_LAUNCHER);i.setComponent(app.component);i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);}catch(Exception ex){Toast.makeText(this,"App unavailable",Toast.LENGTH_SHORT).show();}});
        tile.setOnLongClickListener(v->{if(dockIds.contains(app.id))dockIds.remove(app.id);else if(dockIds.size()<7)dockIds.add(app.id);else {Toast.makeText(this,"Dock allows up to 7 apps",Toast.LENGTH_SHORT).show();return true;}
            persistDock();refreshDock();Toast.makeText(this,"Dock updated",Toast.LENGTH_SHORT).show();return true;});
        return tile;
    }
    private void refreshDock(){
        if(dockRow==null)return;dockRow.removeAllViews();
        int count=0;for(String id:new ArrayList<>(dockIds)){AppItem found=null;for(AppItem item:apps)if(item.id.equals(id)){found=item;break;}
            if(found==null)continue;
            FrameLayout tile=appTile(found,true);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(66),1);lp.setMargins(dp(3),0,dp(3),0);dockRow.addView(tile,lp);
            count++;
        }
        if(count==0){TextView hint=label("Long-press an app to pin it here",12,MUTED,false);dockRow.addView(hint);}
    }
    private void updateClock(){
        if(clockText==null)return;
        Date now=new Date();clockText.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(now));
        dateText.setText(new SimpleDateFormat("EEEE  ·  d MMMM yyyy",Locale.getDefault()).format(now));
        BatteryManager bm=(BatteryManager)getSystemService(BATTERY_SERVICE);
        if(batteryText!=null)batteryText.setText("BATTERY  "+bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)+"%   ·   HYPEROS 3");
    }
    private void openSettings(){try{startActivity(new Intent(Settings.ACTION_SETTINGS));}catch(Exception ignored){}}
    private void requestHome(){
        RoleManager rm=(RoleManager)getSystemService(Context.ROLE_SERVICE);
        if(rm!=null&&rm.isRoleAvailable(RoleManager.ROLE_HOME)){
            if(rm.isRoleHeld(RoleManager.ROLE_HOME)){Toast.makeText(this,"DotDeck is already Home",Toast.LENGTH_SHORT).show();return;}
            try{startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_HOME),REQ_HOME);return;}catch(Exception ignored){}
        }
        try{startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));}
        catch(Exception ex){new AlertDialog.Builder(this).setMessage("HyperOS did not expose a Home app chooser. Keep Xiaomi Home unchanged and run DotDeck as a normal app.").setPositiveButton("OK",null).show();}
    }
    private void chooseWidget(){
        List<AppWidgetProviderInfo> providers=widgetManager.getInstalledProviders();
        if(providers==null||providers.isEmpty()){Toast.makeText(this,"No third-party widgets available",Toast.LENGTH_SHORT).show();return;}
        Collections.sort(providers,(a,b)->String.valueOf(a.loadLabel(getPackageManager())).compareToIgnoreCase(String.valueOf(b.loadLabel(getPackageManager()))));
        String[] labels=new String[providers.size()];for(int i=0;i<providers.size();i++)labels[i]=String.valueOf(providers.get(i).loadLabel(getPackageManager()));
        new AlertDialog.Builder(this).setTitle("Add Android widget").setItems(labels,(dialog,which)->bindWidget(providers.get(which))).setNegativeButton("Cancel",null).show();
    }
    private void bindWidget(AppWidgetProviderInfo info){
        int id=widgetHost.allocateAppWidgetId();pendingWidgetId=id;
        if(widgetManager.bindAppWidgetIdIfAllowed(id,info.provider)){configureOrShow(info,id);return;}
        Intent bind=new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);bind.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);bind.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,info.provider);
        try{startActivityForResult(bind,REQ_BIND);}catch(Exception ex){widgetHost.deleteAppWidgetId(id);pendingWidgetId=-1;Toast.makeText(this,"Widget permission unavailable",Toast.LENGTH_SHORT).show();}
    }
    private void configureOrShow(AppWidgetProviderInfo info,int id){
        if(info.configure!=null){Intent i=new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);i.setComponent(info.configure);i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);
            try{startActivityForResult(i,REQ_CONFIG);return;}catch(Exception ignored){}
        }
        finishWidget(id);
    }
    private void finishWidget(int id){
        int previous=prefs.getInt("widget_id",-1);
        prefs.edit().putInt("widget_id",id).apply();
        if(previous!=-1&&previous!=id){try{widgetHost.deleteAppWidgetId(previous);}catch(Exception ignored){}}
        pendingWidgetId=-1;renderWidget();
    }
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);
        if(request==REQ_BIND||request==REQ_CONFIG){
            int id=data==null?pendingWidgetId:data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,pendingWidgetId);
            if(result!=RESULT_OK){if(pendingWidgetId!=-1)try{widgetHost.deleteAppWidgetId(pendingWidgetId);}catch(Exception ignored){}pendingWidgetId=-1;return;}
            if(request==REQ_BIND){AppWidgetProviderInfo info=widgetManager.getAppWidgetInfo(id);if(info!=null)configureOrShow(info,id);else finishWidget(id);} else finishWidget(id);
        } else if(request==REQ_HOME){Toast.makeText(this,result==RESULT_OK?"Home role granted":"Xiaomi Home remains available",Toast.LENGTH_LONG).show();}
    }
    private void renderWidget(){
        if(widgetSlot==null)return;widgetSlot.removeAllViews();
        int id=prefs.getInt("widget_id",-1);AppWidgetProviderInfo info=id<0?null:widgetManager.getAppWidgetInfo(id);
        if(info!=null){
            try{AppWidgetHostView hostView=widgetHost.createView(this,id,info);widgetSlot.addView(hostView,new FrameLayout.LayoutParams(-1,-1));
                hostView.setOnLongClickListener(v->{new AlertDialog.Builder(this).setTitle("Remove widget?").setNegativeButton("Cancel",null).setPositiveButton("Remove",(d,w)->{widgetHost.deleteAppWidgetId(id);prefs.edit().remove("widget_id").apply();renderWidget();}).show();return true;});return;
            }catch(Exception ignored){}
        }
        TextView placeholder=label("Tap + ADD to place an Android widget here",13,MUTED,false);placeholder.setGravity(Gravity.CENTER);
        widgetSlot.addView(placeholder,new FrameLayout.LayoutParams(-1,-1));
    }
    private LinearLayout row(){LinearLayout v=new LinearLayout(this);v.setOrientation(LinearLayout.HORIZONTAL);return v;}
    private LinearLayout col(){LinearLayout v=new LinearLayout(this);v.setOrientation(LinearLayout.VERTICAL);return v;}
    private int dp(float n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private TextView label(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private TextView chip(String s,int color){TextView t=label(s,11,color,true);t.setGravity(Gravity.CENTER);t.setPadding(dp(12),dp(9),dp(12),dp(9));t.setBackground(roundColor(Color.argb(155,40,46,58),20));return t;}
    private Drawable cardShape(){return roundColor(PANEL,22);}
    private Drawable roundColor(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));d.setStroke(dp(1),Color.argb(35,248,250,255));return d;}
}
