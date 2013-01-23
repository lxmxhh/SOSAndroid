package com.ooxx.sos.view;

import java.util.ArrayList;
import java.util.List;

import com.ooxx.sos.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * **********************************************
 * 
 * @author AUDI
 ************************************************ 
 */
public class BoardView extends View {
	/**
	 * xCount x轴方向的图标数+2
	 */
	protected static  int xCount = Const.levxarray[Const.level];// 可以设置级别
	/**
	 * yCount y轴方向的图表数+2
	 */
	protected static  int yCount = Const.levyarray[Const.level];
	/**
	 * map 连连看游戏棋盘,map中添加的int型在程序中的意思是index，而不是屏幕坐标！
	 */
	protected int[][] map = new int[Const.levxarray[Const.level]][Const.levyarray[Const.level]];
	/**
	 * iconSize 图标大小，图标是正方形，所以一个int变量表示即可
	 */
	protected int iconSize;
	/**
	 * iconCounts 图标的数目
	 */
	protected int iconCounts = 19;
	/**
	 * icons 所有的图片
	 */
	protected Bitmap[] icons = new Bitmap[iconCounts];
	/**
	 * path 可以连通点的路径
	 */
	private Point[] path = null;
	/**
	 * selected 选中的图标
	 */
	protected List<Point> selected = new ArrayList<Point>();

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 */
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		calIconSize();
		Resources r = getResources();
		// 载入连连看中的图标资源
		loadBitmaps(1, r.getDrawable(R.drawable.fruit_01));
		loadBitmaps(2, r.getDrawable(R.drawable.fruit_02));
		loadBitmaps(3, r.getDrawable(R.drawable.fruit_03));
		loadBitmaps(4, r.getDrawable(R.drawable.fruit_04));
		loadBitmaps(5, r.getDrawable(R.drawable.fruit_05));
		loadBitmaps(6, r.getDrawable(R.drawable.fruit_06));
		loadBitmaps(7, r.getDrawable(R.drawable.fruit_07));
		loadBitmaps(8, r.getDrawable(R.drawable.fruit_08));
		loadBitmaps(9, r.getDrawable(R.drawable.fruit_09));
		loadBitmaps(10, r.getDrawable(R.drawable.fruit_10));
		loadBitmaps(11, r.getDrawable(R.drawable.fruit_11));
		loadBitmaps(12, r.getDrawable(R.drawable.fruit_12));
		loadBitmaps(13, r.getDrawable(R.drawable.fruit_13));
		loadBitmaps(14, r.getDrawable(R.drawable.fruit_14));
		loadBitmaps(15, r.getDrawable(R.drawable.fruit_15));
		loadBitmaps(16, r.getDrawable(R.drawable.fruit_17));
		loadBitmaps(17, r.getDrawable(R.drawable.fruit_18));
		loadBitmaps(18, r.getDrawable(R.drawable.fruit_19));
	}

	/**
	 * 计算图标的大小
	 */
	private void calIconSize() {
		// 取得屏幕的大小
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		iconSize = dm.widthPixels / (Const.levxarray[Const.level]);
	}

	/**
	 * 函数目的在于载入图标资源，同时将一个key（特定的整数标识）与一个图标进行绑定
	 * 
	 * @param key
	 *            特定图标的标识
	 * @param d
	 *            drawable下的资源
	 */
	public void loadBitmaps(int key, Drawable d) {
		Bitmap bitmap = Bitmap.createBitmap(iconSize, iconSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, iconSize, iconSize);
		d.draw(canvas);
		icons[key] = bitmap; // 未用0 号index
	}

	/**
	 * View自带的，但是在此方法中，有画路径（删除联通的两个图标）， 绘制棋盘的所有图标（也可理解为刷新，只要此map位置值>0）
	 * 放大第一个选中的图标（selected.size() == 1）
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		/**
		 * 绘制连通路径，然后将路径以及两个图标清除
		 */
		if (path != null && path.length >= 2) {
			for (int i = 0; i < path.length - 1; ++i) {
				Paint paint = new Paint();
				paint.setColor(Color.BLUE);
				paint.setStrokeWidth(3);
				paint.setStyle(Paint.Style.STROKE);
				Point p1 = indexToScreen(path[i].x, path[i].y);
				Point p2 = indexToScreen(path[i + 1].x, path[i + 1].y);
				canvas.drawLine(p1.x + iconSize / 2, p1.y + iconSize / 2, p2.x
						+ iconSize / 2, p2.y + iconSize / 2, paint);
			}
			map[path[0].x][path[0].y] = 0;
			map[path[path.length - 1].x][path[path.length - 1].y] = 0;
			selected.clear();
			path = null;
		}
		/**
		 * 绘制棋盘的所有图标 当这个坐标内的值大于0时绘制
		 */
		for (int x = 1; x < Const.levxarray[Const.level] - 1; ++x) {
			for (int y = 1; y < Const.levyarray[Const.level] - 1; ++y) {
				if (map[x][y] > 0) {
					Point p = indexToScreen(x, y);
					canvas.drawBitmap(icons[map[x][y]], p.x, p.y, null);
				}
			}
		}
		/**
		 * 绘制选中图标，当选中时图标放大显示
		 */
		// for(Point position:selected){
		if (selected.size() > 0) {
			Point position = selected.get(0);
			Point p = indexToScreen(position.x, position.y);
			if (map[position.x][position.y] >= 1) {
				canvas.drawBitmap(icons[map[position.x][position.y]], null,
						new Rect(p.x - 5, p.y - 5, p.x + iconSize + 5, p.y
								+ iconSize + 5), null);
			}
		}
		super.onDraw(canvas);
	}

	/**
	 * 工具方法
	 * 
	 * @param x
	 *            数组中的横坐标
	 * @param y
	 *            数组中的纵坐标
	 * @return 将图标在数组中的坐标转成在屏幕上的真实坐标
	 */
	public Point indexToScreen(int x, int y) {
		return new Point(x * iconSize, y * iconSize);
	}

	/**
	 * 工具方法
	 * 
	 * @param x
	 *            屏幕中的横坐标
	 * @param y
	 *            屏幕中的纵坐标
	 * @return 将图标在屏幕中的坐标转成在数组上的虚拟坐标
	 */
	public Point screenToIndex(int x, int y) {
		int xindex = x / iconSize;
		int yindex = y / iconSize;
		if (xindex < Const.levxarray[Const.level] && yindex < Const.levyarray[Const.level]) {
			return new Point(xindex, yindex);
		} else {
			return new Point(0, 0);
		}
	}

	/**
	 * 传进来path数据更新显示，也就是将能够连接的图标消除
	 * 
	 * @param path
	 */
	public void drawLine(Point[] path) {
		this.path = path;
		this.invalidate();
	}

}
