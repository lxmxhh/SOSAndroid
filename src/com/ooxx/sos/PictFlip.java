package com.ooxx.sos;

import java.io.InputStream;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ooxx.sos.R;
import com.ooxx.sos.view.Const;
import com.ooxx.sos.view.FileUtils;

/**
 * 图片翻页实现
 * 
 * @author Administrator
 * 
 */
public class PictFlip extends Activity implements OnGestureListener {
	private ViewFlipper flipper;
	private GestureDetector detector;
	private int currentScreenIndex;
	private TextView pagecontrol;
	private MyDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.pic);
		detector = new GestureDetector(this);
		flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
		pagecontrol = (TextView) findViewById(R.id.pageControl);
		Const.page = 1;// 修改过来
		pagecontrol.setText("1" + "/" + "5");
		for (int i = 0; i < Const.pic[Const.level].length; i++) {
			flipper.addView(addImgView(Const.pic[Const.level][i]));
		}
	}

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public int getCurrentScreenIndex() {
		return currentScreenIndex;
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, "保存");
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 菜单项选择
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Save();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 保存图片----注意读写SD卡的权限一定要加
	 */
	public void Save() {
		final InputStream isInputStream = getResources().openRawResource(
				Const.pic[Const.level][Const.page-1]);
		final FileUtils fileUtils = new FileUtils();
		fileUtils.getSDPATH();// 给路径赋值
		new AsyncTask<Void, Void, Void>() {
			// 该方法运行在UI线程当中,
			// 主要用于进行异步操作之前的UI准备工作(1111)
			@Override
			protected void onPreExecute() {
				// 显示进度条，在传进来的那个UI控件显示
				super.onPreExecute();
			}

			// 该方法并不运行在UI线程当中，所以在该方法当中，
			// 不能对UI当中的控件进行设置和修改(222)
			// 主要用于进行异步操作,耗时较长的网络操作
			@Override
			protected Void doInBackground(Void... param) {
				fileUtils.write2SDFromInput("SOS", "/" + "sos" + Const.level
						+ Const.page + ".png", isInputStream);
				return null;
			}

			// 在doInBackground方法执行结束之后再运行，
			// 并且运行在UI线程当中(444)
			// 主要用于将异步任务执行的结果展示给客户
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				// 设置进度条不可见
				Toast.makeText(PictFlip.this, "已保存到sdcard下面的sos目录！",
						Toast.LENGTH_SHORT).show();
			}
		}.execute();

	}

	/**
	 * 添加图片
	 * 
	 * @return
	 */
	private View addImgView(int id) {
		ImageView iv = new ImageView(this);
		iv.setImageResource(id);
		return iv;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 120) {// 向左翻
			if (Const.page <= 5 - 1) {
				Const.page++;
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_out));
				this.flipper.showNext();
				pagecontrol.setText(Const.page + "/" + "5");
				return true;
			} else if (Const.page == 5) {
				dialog = new MyDialog(PictFlip.this, "完成!", 100);
				dialog.show();

			} else {
				return false;
			}

		} else if (e1.getX() - e2.getX() < -120) {// 向右翻
			if (Const.page > 1) {
				Const.page--;
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_right_out));
				this.flipper.showPrevious();
				pagecontrol.setText(Const.page + "/" + "5");
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            dialog = new MyDialog(PictFlip.this, "完成!", 100);
            dialog.show();
            return true;
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
		private Context context;

		public MyDialog(Context context, String msg, int time) {
			super(context, R.style.dialog);
			this.context = context;
			this.setContentView(R.layout.dialog_view);
			TextView text_msg = (TextView) findViewById(R.id.text_message);
			TextView text_time = (TextView) findViewById(R.id.text_time);
			TextView btn_menu = (TextView) findViewById(R.id.menu_imgbtn);// 看图
			TextView btn_next = (TextView) findViewById(R.id.next_imgbtn);// 下一关
			TextView btn_replay = (TextView) findViewById(R.id.replay_imgbtn);// 重玩
			if (Const.level == 14) {
				btn_next.setVisibility(View.GONE);
				text_time.setText("恭喜你,顺利通关!"+"\n"+"美女都已经被拯救了哦");
			} else {
				btn_replay.setVisibility(View.VISIBLE);
				btn_menu.setVisibility(View.VISIBLE);
				btn_next.setVisibility(View.VISIBLE);
				text_time.setText("开始下一关?");
				text_msg.setText(msg);
				text_time.setText(text_time.getText().toString()
						.replace("$", String.valueOf(time)));
			}

			btn_menu.setOnClickListener(this);
			btn_next.setOnClickListener(this);
			btn_replay.setOnClickListener(this);
			this.setCancelable(false);
		}

		@Override
		public void onClick(View v) {
			this.dismiss();
			if (v.getId() == R.id.menu_imgbtn) {
//                Const.issuccess=false;
//                Const.count=0;
//                Intent intent = new Intent();
//                intent.setClass(context, PictFlip.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade, R.anim.hold);
			    dismiss();
            } else if (v.getId() == R.id.replay_imgbtn) {
                Const.issuccess=false;
                Const.count=0;
                Const.ispause=false;
                Intent replay = new Intent();
                replay.setClass(PictFlip.this, GameActivity.class);
                Const.xCount = Const.levxarray[Const.level];
                Const.yCount = Const.levyarray[Const.level];
                startActivity(replay);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();
            } else if (v.getId() == R.id.next_imgbtn) {
                Const.issuccess=false;
                Const.count=0;
                Const.ispause=false;
                Intent next = new Intent();
                next.setClass(PictFlip.this, GameActivity.class);
                Const.level++;
                Const.xCount = Const.levxarray[Const.level];
                Const.yCount = Const.levyarray[Const.level];
                startActivity(next);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();
            }
		}
	}
}