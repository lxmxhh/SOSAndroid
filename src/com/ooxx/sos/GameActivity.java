package com.ooxx.sos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ooxx.sos.view.Const;
import com.ooxx.sos.view.GameView;
import com.ooxx.sos.view.OnStateListener;
import com.ooxx.sos.view.OnTimerListener;
import com.ooxx.sos.view.OnToolsChangeListener;
import com.wogamecenter.api.ui.Dashboard;

/**
 * **********************************************
 * 
 * @author AUDI
 ************************************************ 
 */
public class GameActivity extends Activity implements OnToolsChangeListener,
		OnTimerListener, OnStateListener {
	private View backView;

	private ImageButton img_startPlay;
	private ImageView img_title;
	private ImageButton img_woButton;
	private ProgressBar progress;
	public String nString;
	public String nsoundString;
	private MyDialog dialog;
	// visibility at first is "gone"
	private ImageView clock;
	private GameView gameView = null;
	private ImageButton img_tip;
	private ImageButton img_refresh;
	private TextView text_refreshNum;
	private TextView text_tipNum;
	// 两个帮助按键的特效
	private Animation anim = null;

	//private MediaPlayer player;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if ( Const.first == true ) {
				return;
			}
			switch (msg.what) {
			case 0:
				submitScore();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Const.issuccess = true;
						dialog = new MyDialog(GameActivity.this, gameView,
								"完成!", gameView.getTotalTime()
										- progress.getProgress() + 1);
						if (Const.count < 1) {
							dialog.show();
						}
						Const.count++;
					}
				}, 1);
				break;
			case 1:
				submitScore();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Const.issuccess = false;
						dialog = new MyDialog(GameActivity.this, gameView,
								"失败！", gameView.getTotalTime()
										- progress.getProgress() + 1);
						if (Const.count < 1) {
							dialog.show();
						}
						Const.count++;
					}
				}, 1);
				break;
			default:
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 去掉标题显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.game_view);
		anim = AnimationUtils.loadAnimation(this, R.anim.shake);
		findView();
		nString = "暂停";
		if (Const.soundon == true) {
			nsoundString = "声音关";
		} else {
			nsoundString = "声音开";
		}
		GameView.initSound(this);
		img_startPlay.setOnClickListener(new BtnClickListener());
		img_woButton.setOnClickListener(new BtnClickListener());
		gameView.setOnTimerListener(this);
		gameView.setOnStateChangeListener(this);
		gameView.setOnToolsChangedListener(this);
		img_refresh.setOnClickListener(new BtnClickListener());
		img_tip.setOnClickListener(new BtnClickListener());
		if (Const.first == true) {// 如果是第一次启动
			startView();
		} else {
//			player = MediaPlayer.create(this, R.raw.bg);
//			player.setLooping(true);// 设置循环播放
//			if (Const.soundon) {
//				player.start();
//			}
			playingView();
		}

	}// end of the OnCreate method!

	/**
	 * 寻找对应资源控件
	 */
	public void findView() {
		backView = (View) findViewById(R.id.back);
		clock = (ImageView) this.findViewById(R.id.clock);
		progress = (ProgressBar) this.findViewById(R.id.timer);
		img_title = (ImageView) this.findViewById(R.id.title_img);
		img_startPlay = (ImageButton) this.findViewById(R.id.play_btn);
		img_woButton = (ImageButton) this.findViewById(R.id.wo_btn);
		img_tip = (ImageButton) this.findViewById(R.id.tip_btn);
		img_refresh = (ImageButton) this.findViewById(R.id.refresh_btn);
		gameView = (GameView) this.findViewById(R.id.game_view);
		text_refreshNum = (TextView) this.findViewById(R.id.text_refresh_num);
		text_tipNum = (TextView) this.findViewById(R.id.text_tip_num);
	}

	/**
	 * 程序开启界面显示
	 */
	public void startView() {
		if (Const.first == false) {
			backView.setBackgroundResource(R.drawable.background);
			img_title.setVisibility(View.GONE);
			gameView.setVisibility(View.VISIBLE);
		} else {
			img_title.setVisibility(View.GONE);
			backView.setBackgroundResource(R.drawable.start1);
		}
		//Const.first = false;
		Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
		img_title.startAnimation(scale);
		img_startPlay.startAnimation(scale);

//		player = MediaPlayer.create(this, R.raw.bg);
//		player.setLooping(true);// 设置循环播放
//		if (Const.soundon) {
//			player.start();
//		}
	}

	/**
	 * 游戏运行时界面显示,即连连看的布局
	 */
	public void playingView() {
		Const.first = false;
		backView.setBackgroundResource(R.drawable.background);// //////////////////////////////////////////////
		Animation scaleOut = AnimationUtils.loadAnimation(this,
				R.anim.scale_anim_out);
		img_title.startAnimation(scaleOut);
		img_startPlay.startAnimation(scaleOut);
		img_title.setVisibility(View.GONE);
		img_startPlay.setVisibility(View.GONE);
		//img_woButton.setVisibility(View.GONE);

		progress.setMax(gameView.getTotalTime());
		progress.setProgress(gameView.getTotalTime());

		clock.setVisibility(View.VISIBLE);
		progress.setVisibility(View.VISIBLE);
		gameView.setVisibility(View.VISIBLE);
		img_tip.setVisibility(View.VISIBLE);
		img_refresh.setVisibility(View.VISIBLE);
		text_tipNum.setVisibility(View.VISIBLE);
		text_refreshNum.setVisibility(View.VISIBLE);
		Animation animIn = AnimationUtils.loadAnimation(this, R.anim.trans_in);
		gameView.startAnimation(animIn);
		img_tip.startAnimation(animIn);
		img_refresh.startAnimation(animIn);
		text_tipNum.startAnimation(animIn);
		text_refreshNum.startAnimation(animIn);
		//player.pause();
		gameView.startPlay();
		toast();
	}

	/**
	 * 一个处理开始游戏，刷新，帮助三个按钮的listener的类
	 * 
	 * @author AUDI
	 * 
	 */
	class BtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.play_btn) {
				Log.d("ooxxx","click play");
                playingView();
            } else if (v.getId() == R.id.wo_btn ) {
            	Log.d("ooxxx","open dashboard");
            	Dashboard.open();
            	
			} else if (v.getId() == R.id.refresh_btn) {
                if (Const.ispause == false && Const.issuccess == false) {
					img_refresh.startAnimation(anim);
					gameView.refreshChange();
					gameView.invalidate();
				}
            } else if (v.getId() == R.id.tip_btn) {
                if (Const.ispause == false && Const.issuccess == false) {
					img_tip.startAnimation(anim);
					gameView.autoHelp();
				}
            }
		}
	}

	@Override
	public void onRefreshChanged(int count) {
		text_refreshNum.setText("" + gameView.getRefreshNum());
	}

	@Override
	public void onTipChanged(int count) {
		text_tipNum.setText("" + gameView.getTipNum());
	}

	@Override
	public void onTimer(int leftTime) {
		progress.setProgress(leftTime);

	}

	/**
	 * 用来控制音乐的播放
	 */
	@Override
	public void OnStateChanged(int StateMode) {
		switch (StateMode) {
		case GameView.WIN:
			Const.issuccess = true;
			handler.sendEmptyMessage(0);
			break;
		case GameView.LOSE:
			Const.issuccess = false;
			handler.sendEmptyMessage(1);
			break;
		case GameView.PAUSE:
			//player.stop();
			//gameView.player.pause();
			gameView.stopTimer();
			break;
		case GameView.QUIT:
			//player.release();
			//gameView.player.release();
			gameView.stopTimer();
			break;
		case GameView.CONTINUE:
			//gameView.player.start();
			break;
		}
	}

	public void quit() {
		this.finish();
	}

	/**
	 * 用于提醒游戏开始，提醒总时间
	 */
	public void toast() {
		Toast.makeText(this,
				"你有 " + gameView.getTotalTime() + "s" + "的时间去拯救，快去吧！",
				Toast.LENGTH_SHORT).show();
	}

	private boolean realPause;
	private MenuItem pauseItem;

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Const.ispause = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Const.ispause = true;
		gameView.setMode(GameView.PAUSE);
		realPause = true;
		if(pauseItem != null){
		    pauseItem.setTitle("继续");
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        if (Const.ispause == true) {
            nString = "继续";
        }
        if(realPause){
            nString = "暂停";
            Const.ispause = false;
            gameView.setContinue();
            if (Const.soundon) {
                //player.start();
                //gameView.player.start();
            }
            if(pauseItem != null){
                pauseItem.setTitle("暂停");
            }
        }
    }

	@Override
	protected void onDestroy() {
		Const.ispause = false;
		super.onDestroy();
		gameView.setMode(GameView.QUIT);
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, "重玩");
		pauseItem = menu.add(Menu.NONE, 2, Menu.NONE, nString);
		menu.add(Menu.NONE, 3, Menu.NONE, "Wo+社区");
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 菜单项选择
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1: // 好像只有新开一个对话框才会实现重新开始
			gameView.stopTimer();
			AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
			dialog1.setTitle("重玩");
			dialog1.setMessage("重玩本关游戏?");
			dialog1.setPositiveButton("否",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Const.count = 0;
							gameView.setContinue();
							if (Const.soundon) {
								//player.start();
								//gameView.player.start();
							}
						}
					}).setNeutralButton("是",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Const.count = 0;
							Const.issuccess = false;
							gameView.startPlay();
						}
					});
			dialog1.show();
			break;
		case 2:
			if (Const.ispause == true) {
				nString = "暂停";
				item.setTitle(nString);
				Const.ispause = false;
				gameView.setContinue();
				if (Const.soundon) {
//					player.start();
//					gameView.player.start();
				}
			} else if (Const.ispause == false) {
				nString = "继续";
				Const.ispause = true;
				item.setTitle(nString);
				gameView.stopTimer();
			}
			break;
		case 3:
			// /////////////////////////
//			if (item.getTitle().equals("声音关")) {
//				Const.soundon = false;
//				item.setTitle("声音开");
//				if (player.isPlaying())
//					player.stop();
//				if (gameView.player.isPlaying())
//					gameView.player.pause();
//			} else if (item.getTitle().equals("声音开")) {
//				item.setTitle("声音关");
//				Const.soundon = true;
//				gameView.player.start();
//			}
			Dashboard.open();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 监听后退按钮,以防止误按，按下back按钮后，程序应当处于暂停状态
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			gameView.stopTimer();
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					GameActivity.this)
					.setTitle("退出游戏")
					.setMessage("确定退出游戏？")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
//									Intent startMain = new Intent(
//											Intent.ACTION_MAIN);
//									startMain.addCategory(Intent.CATEGORY_HOME);
//									startMain
//											.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//									startActivity(startMain);
									System.exit(0);
//									android.os.Process
//											.killProcess(android.os.Process
//													.myPid()); // 获取PID
								}
							})
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									gameView.setContinue();
									//if (Const.soundon) {
										//player.start();
									//}
									//gameView.player.start();
								}
							});
			dialog.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 成功对话框
	 * 
	 * @author Administrator
	 * 
	 */
	class MyDialog extends Dialog implements OnClickListener {

		private GameView gameview;
		private Context context;

		public MyDialog(Context context, GameView gameview, String msg, int time) {
			super(context, R.style.dialog);
			this.gameview = gameview;
			this.context = context;
			this.setContentView(R.layout.dialog_view);
			TextView text_msg = (TextView) findViewById(R.id.text_message);
			TextView text_time = (TextView) findViewById(R.id.text_time);
			TextView btn_menu = (TextView) findViewById(R.id.menu_imgbtn);// 看图
			TextView btn_next = (TextView) findViewById(R.id.next_imgbtn);// 下一关
			TextView btn_replay = (TextView) findViewById(R.id.replay_imgbtn);// 重玩
			if (Const.level == Const.totallevels - 1 && Const.issuccess == true) {
				btn_next.setVisibility(View.GONE);
				btn_menu.setVisibility(View.VISIBLE);
				text_time.setText("恭喜你,顺利通关!");
			} else if (Const.issuccess == false) {// 输
				btn_replay.setVisibility(View.VISIBLE);
				btn_next.setVisibility(View.VISIBLE);
				text_time.setText(R.string.time1);
			} else if (Const.issuccess == true) {// 赢
				btn_replay.setVisibility(View.VISIBLE);
				btn_menu.setVisibility(View.VISIBLE);
				btn_next.setVisibility(View.VISIBLE);
			}
			text_msg.setText(msg);
			text_time.setText(text_time.getText().toString()
					.replace("$", String.valueOf(time)));
			btn_menu.setOnClickListener(this);
			btn_next.setOnClickListener(this);
			btn_replay.setOnClickListener(this);
			this.setCancelable(false);
		}

		@Override
		public void onClick(View v) {
			this.dismiss();
			if (v.getId() == R.id.menu_imgbtn) {
                Const.issuccess = false;
                Const.count = 0;
                Intent intent = new Intent();
                intent.setClass(context, PictFlip.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            } else if (v.getId() == R.id.replay_imgbtn) {
                Const.count = 0;
                Const.issuccess = false;
                gameview.startPlay();
            } else if (v.getId() == R.id.next_imgbtn) {
                Const.count = 0;
                Const.issuccess = false;
                Const.level++;
                Intent next = new Intent();
                next.setClass(GameActivity.this, GameActivity.class);
                Const.xCount = Const.levxarray[Const.level];
                Const.yCount = Const.levyarray[Const.level];
                startActivity(next);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();
            }
		}
	}

	/*
	 * 
	 */
	private void submitScore() {
		Log.e("cxs", "Const.level="+Const.level);

		WogameTools.submitScore(GameActivity.this, WogameTools.LeadboardID_1, Const.level+1, Const.level+1+"关");

		switch (Const.level) {
		case 2:
			if((gameView.getTotalTime() - progress.getProgress() + 1)<=20){
				WogameTools.UnlockAchievement(GameActivity.this, WogameTools.AchievementID[0]);
			}
			break;
		case 5:
			if((gameView.getTotalTime() - progress.getProgress() + 1)<=25){
				WogameTools.UnlockAchievement(GameActivity.this, WogameTools.AchievementID[0]);
			}
			break;
		case 8:
			if((gameView.getTotalTime() - progress.getProgress() + 1)<=30){
				WogameTools.UnlockAchievement(GameActivity.this, WogameTools.AchievementID[0]);
			}
			break;
		case 11:
			if((gameView.getTotalTime() - progress.getProgress() + 1)<=35){
				WogameTools.UnlockAchievement(GameActivity.this, WogameTools.AchievementID[0]);
			}
			break;
		case 14:
			if((gameView.getTotalTime() - progress.getProgress() + 1)<=40){
				WogameTools.UnlockAchievement(GameActivity.this, WogameTools.AchievementID[0]);
			}
			break;

		default:
			break;
		}
	}
}
