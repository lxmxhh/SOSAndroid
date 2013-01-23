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
 * 此类继承自自定义的BoardView，完成了hint的自动帮助功能（主要是使用path全局变量，在die方法跟link方法之间将数据添加进去）
 * 实现了游戏关键的核心算法，即判断两个按下的图标是否按照游戏规则能否连通。 添加了一个实时更新时间的线程，以及使用Handler更新游戏布局
 * 添加了OnTouchEvent，整个程序的游戏布局都是按照坐标进行的完全重绘
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
	 * 提示帮助的次数
	 */
	private int help = Const.help[Const.level];
	/**
	 * 刷新的次数
	 */
	private int refresh = Const.refresh[Const.level];
	/**
	 * 第一关的时间是100s
	 */
	private int totalTime = Const.totalTime;
	private int leftTime;

	//public static MusicPlayer musicPlay;
	//public MediaPlayer player;

	private RefreshTime refreshTime;
	private RefreshHandler refreshHandler = new RefreshHandler();
	/**
	 * 用来停止计时器的线程
	 */
	private boolean isStop = false;
	private boolean isContinue = true;
	private OnTimerListener timerListener = null;
	private OnStateListener stateListener = null;
	private OnToolsChangeListener toolsChangedListener = null;
	private List<Point> path = new ArrayList<Point>();
	Thread t = null;
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 */
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		player = MediaPlayer.create(context, R.raw.back2new);
//		player.setLooping(true);// 设置循环播放
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
		t = new Thread(refreshTime); // 注意正确启动一个实现Runnable接口的线程类
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
				} else if (die()) { // 调用一次die方法！此时如果die返回为false，即还能够连通
					change(); // 由于die中使用link方法检测，所以此时path中的值又添加了进去，
				} // 这对于我们使用autoHelp方法提供便利！！！
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
	 * 用于更新剩余时间的线程
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
	 * 停止显示的时间
	 */
	public void stopTimer() {
		isStop = true;
		isContinue = false;
	}

	/**
	 * 设置继续
	 */
	public void setContinue() {
		isContinue = true;
		isStop = false;
		refreshTime = new RefreshTime();
		Thread t = new Thread(refreshTime); // 注意正确启动一个实现Runnable接口的线程
		t.start();
	}

	/**
	 * 判断是否已经完成任务
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
	 * 用于判断是否当前已经无解
	 */
	public boolean die() {
		for (int y = 1; y < Const.levyarray[Const.level]; y++)
			// 表示从此行中的一个元素开始扫描（起点）
			for (int x = 1; x < Const.levxarray[Const.level]; x++) { // 表示此行中指定列，组成扫描起点
				if (map[x][y] != 0) {
					for (int j = y; j < Const.levyarray[Const.level]; j++) {// 表示正在被扫描的行
						if (j == y) {// 循环中的第一次扫描，为什么特殊？因为此时不一定从一行中的第一个元素开始扫描
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
	 * 设定状态
	 * 
	 * @param stateMode
	 */
	public void setMode(int stateMode) {
		this.stateListener.OnStateChanged(stateMode);
	}

	/**
	 * 初始化地图
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
	 * 随机将现有的布局打乱，重新布局，map中现有图标数量不变，相当于一次refresh
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
	 * 对于选择的处理,如果是第一次按下，则将其加入到selected当中， 若是第二次（selected.size()==1），则先判断能不能连通
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Const.ispause == false) {
			int sx = (int) event.getX();
			int sy = (int) event.getY();
			Point p = screenToIndex(sx, sy);
			if (map[p.x][p.y] != 0) {
				if (selected.size() == 1) {
					if (link(selected.get(0), p)) { // 能够连通，path中的数据是在link判断时如果返回真，方法内部就已经将数据添加进去
						selected.add(p);
						drawLine(path.toArray(new Point[] {}));
//						if (Const.soundon) {
//							musicPlay.play(ID_SOUND_DISAPEAR, 0);
//						}
						refreshHandler.sendRefresh(500);
					} else {// 不能够连通
						selected.clear();
						selected.add(p);
//						if (Const.soundon) {
//							musicPlay.play(ID_SOUND_CHOOSE, 0);
//						}
						GameView.this.invalidate(); // 在这儿说一下refreshHanler.sendRefresh(int)
													// 跟单纯调用GameView.this.invalidate()区别
													// 前者除了后者只拥有的刷新显示之外，还加了是否已经无解或者已经完成任务的判断的操作。
					}
				} else {// 此时的selected中的size只能等于0
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
	 * 本游戏的核心算法,判断两个连接点是否能够连接，这里传进来的就是我们点击的两个点转化成index的值
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
			 * 一个拐点的判断
			 */
			Point px = new Point(p1.x, p2.y); // 假设第一种可能点
			if (map[p1.x][p2.y] == 0 && linkDirect(p1, px)
					&& linkDirect(px, p2)) {
				path.add(p1);
				path.add(px);
				path.add(p2);
				return true;
			}
			Point py = new Point(p2.x, p1.y); // 假设第二种可能点
			if (map[p2.x][p1.y] == 0 && linkDirect(p1, py)
					&& linkDirect(py, p2)) {// 首先判断map[p2.x][p1.y]中介点是否有图标
				path.add(p1);
				path.add(py);
				path.add(p2);
				return true;
			}

			/**
			 * 两个corner
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
			return false; // 最后三种方式都不能连通，还是要return false ,不然在两个同样的图标下却没有返回值!
		}
		return false;
	}

	/**
	 * 判断直线链接，无拐角,传进来的点值是ScreenToIndex过的了,不过这里传进来的不一定就是我们点击的点，也可能是我们的拐角点
	 * 
	 * @param p1
	 * @param p2
	 */
	public boolean linkDirect(Point p1, Point p2) {
		// 纵向直线
		if (p1.x == p2.x) {
			int y1 = Math.min(p1.y, p2.y);
			int y2 = Math.max(p1.y, p2.y);
			boolean flag = true;
			for (int y = y1 + 1; y < y2; y++) {// 这个循环里容易漏掉两个相邻的情况，所以才加上上面的flag样式
				if (map[p1.x][y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		// 横直线判断
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
	 * 向x方向扩展，传进来的点是index过的
	 * 
	 * @param p
	 * @param list
	 */
	public void expandX(Point p, List<Point> list) {
		list.clear();
		for (int x = p.x + 1; x < Const.levxarray[Const.level]; x++) {// 注意此时可以等于xCount
																		// -1了
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
	 * 向Y方向扩展，传进来的点是index过的，而list是作为“返回值”需要保存的值
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
	 * 当点击help按钮时候调用，会帮助玩家消除一对图标
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
	 * 当点击refresh按钮后调用
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
	 * 设置游戏时间限制，只要是各个关卡
	 * 
	 * @param time
	 */
	public void setTotalTime(int time) {
		this.totalTime = time;
	}

	/**
	 * 下一关
	 */
	public void startNextPlay() {
		totalTime -= 10;
		startPlay();
	}

	/**
	 * 得到游戏的总时间，用于设置进度条的最大值等
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
