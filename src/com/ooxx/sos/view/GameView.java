package com.ooxx.sos.view;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import com.ooxx.sos.MusicPlayer;
import com.ooxx.sos.R;
import com.ooxx.sos.UMengEvent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ����̳����Զ����BoardView�������hint���Զ��������ܣ���Ҫ��ʹ��pathȫ�ֱ�������die������link����֮�佫������ӽ�ȥ��
 * ʵ������Ϸ�ؼ��ĺ����㷨�����ж��������µ�ͼ���Ƿ�����Ϸ�����ܷ���ͨ�� �����һ��ʵʱ����ʱ����̣߳��Լ�ʹ��Handler������Ϸ����
 * �����OnTouchEvent�������������Ϸ���ֶ��ǰ���������е���ȫ�ػ�
 * 
 * @author AUDI
 * 
 */
public class GameView extends BoardView {

	private static final int REFRESH_VIEW = 1;

	public static final int WIN = 1;
	public static final int LOSE = 2;
	public static final int PAUSE = 3;
	public static final int PLAY = 4;
	public static final int QUIT = 5;
	public static final int CONTINUE = 6;

	public static final int ID_SOUND_CHOOSE = 0;
	public static final int ID_SOUND_DISAPEAR = 1;
	public static final int ID_SOUND_WIN = 4;
	public static final int ID_SOUND_LOSE = 5;
	public static final int ID_SOUND_REFRESH = 6;
	public static final int ID_SOUND_TIP = 7;
	public static final int ID_SOUND_ERROR = 8;

	/**
	 * ��ʾ�����Ĵ���
	 */
	private int help = Const.help[Const.level];
	/**
	 * ˢ�µĴ���
	 */
	private int refresh = Const.refresh[Const.level];
	/**
	 * ��һ�ص�ʱ����100s
	 */
	private int totalTime = Const.totalTime;
	private int leftTime;

	//public static MusicPlayer musicPlay;
	//public MediaPlayer player;

	private RefreshTime refreshTime;
	private RefreshHandler refreshHandler = new RefreshHandler();
	/**
	 * ����ֹͣ��ʱ�����߳�
	 */
	private boolean isStop = false;
	private boolean isContinue = true;
	private OnTimerListener timerListener = null;
	private OnStateListener stateListener = null;
	private OnToolsChangeListener toolsChangedListener = null;
	private List<Point> path = new ArrayList<Point>();
	Thread t = null;
	
	/**
	 * ���캯��
	 * 
	 * @param context
	 * @param attrs
	 */
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		player = MediaPlayer.create(context, R.raw.back2new);
//		player.setLooping(true);// ����ѭ������
	}

	public void startPlay() {
		help = Const.help[Const.level];
		refresh = Const.refresh[Const.level];
		isContinue = true;
		isStop = false;
		toolsChangedListener.onRefreshChanged(refresh);
		toolsChangedListener.onTipChanged(help);
		leftTime = totalTime;
		initMap();
//		if (Const.soundon) {
//			player.start();
//		}
		refreshTime = new RefreshTime();
		t = new Thread(refreshTime); // ע����ȷ����һ��ʵ��Runnable�ӿڵ��߳���
		t.start();
		GameView.this.invalidate();
	}

	public static void initSound(Context context) {
//		musicPlay = new MusicPlayer();
//		musicPlay.initSounds(context);
//		musicPlay.loadSfx(context, R.raw.choose, ID_SOUND_CHOOSE);
//		musicPlay.loadSfx(context, R.raw.disappear1, ID_SOUND_DISAPEAR);
//		musicPlay.loadSfx(context, R.raw.win, ID_SOUND_WIN);
//		musicPlay.loadSfx(context, R.raw.lose, ID_SOUND_LOSE);
//		musicPlay.loadSfx(context, R.raw.item1, ID_SOUND_REFRESH);
//		musicPlay.loadSfx(context, R.raw.item2, ID_SOUND_TIP);
//		musicPlay.loadSfx(context, R.raw.alarm, ID_SOUND_ERROR);
	}

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == REFRESH_VIEW) {
				GameView.this.invalidate();
				if (win()) {
					setMode(WIN);
//					if (Const.soundon) {
//						musicPlay.play(ID_SOUND_WIN, 0);
//					}
					isStop = true;
					isContinue = false;
				} else if (die()) { // ����һ��die��������ʱ���die����Ϊfalse�������ܹ���ͨ
					change(); // ����die��ʹ��link������⣬���Դ�ʱpath�е�ֵ������˽�ȥ��
				} // ���������ʹ��autoHelp�����ṩ����������
			}
		}

		/**
		 * 
		 * @param delayTime
		 */
		public void sendRefresh(int delayTime) {
			Message msg = new Message();
			this.removeMessages(0);
			msg.what = REFRESH_VIEW;
			this.sendMessageDelayed(msg, delayTime);
		}
	}

	/**
	 * ���ڸ���ʣ��ʱ����߳�
	 * 
	 * @author AUDI
	 * 
	 */
	class RefreshTime implements Runnable {

		@Override
		public void run() {
			if (isContinue) {
				while (leftTime > 0 && !isStop) {
					timerListener.onTimer(leftTime);
					leftTime--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (isStop && leftTime > 0) {
				if (win())
					;// setMode(WIN);
				else
					setMode(PAUSE);
			}
			// setMode(LOSE);
			else if (leftTime == 0) {
				setMode(LOSE);
//				if (Const.soundon) {
//					musicPlay.play(ID_SOUND_LOSE, 0);
//				}
			}
		}
	}

	/**
	 * ֹͣ��ʾ��ʱ��
	 */
	public void stopTimer() {
		isStop = true;
		isContinue = false;
	}

	/**
	 * ���ü���
	 */
	public void setContinue() {
		isContinue = true;
		isStop = false;
		refreshTime = new RefreshTime();
		Thread t = new Thread(refreshTime); // ע����ȷ����һ��ʵ��Runnable�ӿڵ��߳�
		t.start();
	}

	/**
	 * �ж��Ƿ��Ѿ��������
	 * 
	 * @return
	 */
	public boolean win() {
		for (int x = 1; x < Const.levxarray[Const.level] - 1; x++)
			for (int y = 1; y < Const.levyarray[Const.level] - 1; y++) {
				if (map[x][y] >= 1)
					return false;
			}
		return true;
	}

	/**
	 * �����ж��Ƿ�ǰ�Ѿ��޽�
	 */
	public boolean die() {
		for (int y = 1; y < Const.levyarray[Const.level]; y++)
			// ��ʾ�Ӵ����е�һ��Ԫ�ؿ�ʼɨ�裨��㣩
			for (int x = 1; x < Const.levxarray[Const.level]; x++) { // ��ʾ������ָ���У����ɨ�����
				if (map[x][y] != 0) {
					for (int j = y; j < Const.levyarray[Const.level]; j++) {// ��ʾ���ڱ�ɨ�����
						if (j == y) {// ѭ���еĵ�һ��ɨ�裬Ϊʲô���⣿��Ϊ��ʱ��һ����һ���еĵ�һ��Ԫ�ؿ�ʼɨ��
							for (int i = x + 1; i < Const.levxarray[Const.level] - 1; i++) {
								if (map[x][y] == map[i][j]
										&& link(new Point(x, y),
												new Point(i, j))) {
									return false;
								}
							}
						} else {
							for (int i = 1; i < Const.levxarray[Const.level] - 1; i++) {
								if (map[x][y] == map[i][j]
										&& link(new Point(x, y),
												new Point(i, j)))
									return false;
							}
						}
					}
				}
			}
		return true;
	}

	/**
	 * �趨״̬
	 * 
	 * @param stateMode
	 */
	public void setMode(int stateMode) {
		this.stateListener.OnStateChanged(stateMode);
	}

	/**
	 * ��ʼ����ͼ
	 */
	public void initMap() {
		int x = 1;
		int y = 0;
		for (int i = 1; i < Const.levxarray[Const.level] - 1; i++)
			for (int j = 1; j < Const.levyarray[Const.level] - 1; j++) {
				map[i][j] = x;
				if (y == 1) {
					x++;
					y = 0;
					if (x == iconCounts) {
						x = 1;
					}
				} else {
					y = 1;
				}
			}
		change();
		GameView.this.invalidate();
	}

	/**
	 * ��������еĲ��ִ��ң����²��֣�map������ͼ���������䣬�൱��һ��refresh
	 */
	public void change() {
		Random random = new Random();
		int tmp, xtmp, ytmp;
		for (int x = 1; x < Const.levxarray[Const.level] - 1; x++) {
			for (int y = 1; y < Const.levyarray[Const.level] - 1; y++) {
				xtmp = 1 + random.nextInt(Const.levxarray[Const.level] - 2);
				ytmp = 1 + random.nextInt(Const.levyarray[Const.level] - 2);
				tmp = map[x][y];
				map[x][y] = map[xtmp][ytmp];
				map[xtmp][ytmp] = tmp;
			}
		}
		if (die()) {
			change();
		}
	}

	/**
	 * ����ѡ��Ĵ���,����ǵ�һ�ΰ��£�������뵽selected���У� ���ǵڶ��Σ�selected.size()==1���������ж��ܲ�����ͨ
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Const.ispause == false) {
			int sx = (int) event.getX();
			int sy = (int) event.getY();
			Point p = screenToIndex(sx, sy);
			if (map[p.x][p.y] != 0) {
				if (selected.size() == 1) {
					if (link(selected.get(0), p)) { // �ܹ���ͨ��path�е���������link�ж�ʱ��������棬�����ڲ����Ѿ���������ӽ�ȥ
						selected.add(p);
						drawLine(path.toArray(new Point[] {}));
//						if (Const.soundon) {
//							musicPlay.play(ID_SOUND_DISAPEAR, 0);
//						}
						refreshHandler.sendRefresh(500);
					} else {// ���ܹ���ͨ
						selected.clear();
						selected.add(p);
//						if (Const.soundon) {
//							musicPlay.play(ID_SOUND_CHOOSE, 0);
//						}
						GameView.this.invalidate(); // �����˵һ��refreshHanler.sendRefresh(int)
													// ����������GameView.this.invalidate()����
													// ǰ�߳��˺���ֻӵ�е�ˢ����ʾ֮�⣬�������Ƿ��Ѿ��޽�����Ѿ����������жϵĲ�����
					}
				} else {// ��ʱ��selected�е�sizeֻ�ܵ���0
					selected.add(p);
//					if (Const.soundon) {
//						musicPlay.play(ID_SOUND_CHOOSE, 0);
//					}
					GameView.this.invalidate();
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * ����Ϸ�ĺ����㷨,�ж��������ӵ��Ƿ��ܹ����ӣ����ﴫ�����ľ������ǵ����������ת����index��ֵ
	 * 
	 * @param p1
	 * @param p2
	 */
	List<Point> p1Expand = new ArrayList<Point>();
	List<Point> p2Expand = new ArrayList<Point>();

	public boolean link(Point p1, Point p2) {
		if (p1.equals(p2)) {
			return false;
		}
		path.clear();
		if (map[p1.x][p1.y] == map[p2.x][p2.y]) {
			if (linkDirect(p1, p2)) {
				path.add(p1);
				path.add(p2);
				return true;
			}
			/**
			 * һ���յ���ж�
			 */
			Point px = new Point(p1.x, p2.y); // �����һ�ֿ��ܵ�
			if (map[p1.x][p2.y] == 0 && linkDirect(p1, px)
					&& linkDirect(px, p2)) {
				path.add(p1);
				path.add(px);
				path.add(p2);
				return true;
			}
			Point py = new Point(p2.x, p1.y); // ����ڶ��ֿ��ܵ�
			if (map[p2.x][p1.y] == 0 && linkDirect(p1, py)
					&& linkDirect(py, p2)) {// �����ж�map[p2.x][p1.y]�н���Ƿ���ͼ��
				path.add(p1);
				path.add(py);
				path.add(p2);
				return true;
			}

			/**
			 * ����corner
			 */
			expandX(p1, p1Expand);
			expandX(p2, p2Expand);
			for (int i = 0; i < p1Expand.size(); i++)
				for (int j = 0; j < p2Expand.size(); j++) {
					if (p1Expand.get(i).x == p2Expand.get(j).x) {
						if (linkDirect(p1Expand.get(i), p2Expand.get(j))) {
							path.add(p1);
							path.add(p1Expand.get(i));
							path.add(p2Expand.get(j));
							path.add(p2);
							return true;
						}
					}
				}

			expandY(p1, p1Expand);
			expandY(p2, p2Expand);
			for (Point exp1 : p1Expand)
				for (Point exp2 : p2Expand) {
					if (exp1.y == exp2.y) {
						if (linkDirect(exp1, exp2)) {
							path.add(p1);
							path.add(exp1);
							path.add(exp2);
							path.add(p2);
							return true;
						}
					}
				}
			return false; // ������ַ�ʽ��������ͨ������Ҫreturn false ,��Ȼ������ͬ����ͼ����ȴû�з���ֵ!
		}
		return false;
	}

	/**
	 * �ж�ֱ�����ӣ��޹ս�,�������ĵ�ֵ��ScreenToIndex������,�������ﴫ�����Ĳ�һ���������ǵ���ĵ㣬Ҳ���������ǵĹսǵ�
	 * 
	 * @param p1
	 * @param p2
	 */
	public boolean linkDirect(Point p1, Point p2) {
		// ����ֱ��
		if (p1.x == p2.x) {
			int y1 = Math.min(p1.y, p2.y);
			int y2 = Math.max(p1.y, p2.y);
			boolean flag = true;
			for (int y = y1 + 1; y < y2; y++) {// ���ѭ��������©���������ڵ���������Բż��������flag��ʽ
				if (map[p1.x][y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		// ��ֱ���ж�
		if (p1.y == p2.y) {
			int x1 = Math.min(p1.x, p2.x);
			int x2 = Math.max(p1.x, p2.x);
			boolean flag = true;
			for (int x = x1 + 1; x < x2; x++) {
				if (map[x][p1.y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ��x������չ���������ĵ���index����
	 * 
	 * @param p
	 * @param list
	 */
	public void expandX(Point p, List<Point> list) {
		list.clear();
		for (int x = p.x + 1; x < Const.levxarray[Const.level]; x++) {// ע���ʱ���Ե���xCount
																		// -1��
			if (map[x][p.y] != 0)
				break;
			list.add(new Point(x, p.y));
		}
		for (int x = p.x - 1; x >= 0; x--) {
			if (map[x][p.y] != 0)
				break;
			list.add(new Point(x, p.y));
		}
	}

	/**
	 * ��Y������չ���������ĵ���index���ģ���list����Ϊ������ֵ����Ҫ�����ֵ
	 * 
	 * @param p
	 * @param list
	 */
	public void expandY(Point p, List<Point> list) {
		list.clear();
		for (int y = p.y + 1; y < Const.levyarray[Const.level]; y++) {
			if (map[p.x][y] != 0)
				break;
			list.add(new Point(p.x, y));
		}
		for (int y = p.y - 1; y >= 0; y--) {
			if (map[p.x][y] != 0)
				break;
			list.add(new Point(p.x, y));
		}
	}

	/**
	 * �����help��ťʱ����ã�������������һ��ͼ��
	 */
	public void autoHelp() {
		if (help == 0) {
//			if (Const.soundon) {
//				musicPlay.play(ID_SOUND_ERROR, 0);
//			}
			return;
		} else {
//			if (Const.soundon) {
//				musicPlay.play(ID_SOUND_TIP, 0);
//			}
			help--;
			toolsChangedListener.onTipChanged(help);
			drawLine(path.toArray(new Point[] {}));
			refreshHandler.sendRefresh(500);
		}
	}

	/**
	 * �����refresh��ť�����
	 */
	public void refreshChange() {
		if (refresh == 0) {
//			if (Const.soundon) {
//				musicPlay.play(ID_SOUND_ERROR, 0);
//			}
			return;
		} else {
//			if (Const.soundon) {
//				musicPlay.play(ID_SOUND_REFRESH, 0);
//			}
			refresh--;
			toolsChangedListener.onRefreshChanged(refresh);
			change();
		}
	}

	/**
	 * ������Ϸʱ�����ƣ�ֻҪ�Ǹ����ؿ�
	 * 
	 * @param time
	 */
	public void setTotalTime(int time) {
		this.totalTime = time;
	}

	/**
	 * ��һ��
	 */
	public void startNextPlay() {
		totalTime -= 10;
		startPlay();
	}

	/**
	 * �õ���Ϸ����ʱ�䣬�������ý����������ֵ��
	 * 
	 * @return
	 */
	public int getTotalTime() {
		return totalTime;
	}

	public int getTipNum() {
		return help;
	}

	public int getRefreshNum() {
		return refresh;
	}

	public void setOnTimerListener(OnTimerListener onTimerListener) {
		this.timerListener = onTimerListener;
	}

	public void setOnToolsChangedListener(
			OnToolsChangeListener toolsChangeListener) {
		this.toolsChangedListener = toolsChangeListener;
	}

	public void setOnStateChangeListener(OnStateListener stateListener) {
		this.stateListener = stateListener;
	}
}
