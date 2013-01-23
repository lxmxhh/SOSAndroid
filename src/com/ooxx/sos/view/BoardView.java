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
	 * xCount x�᷽���ͼ����+2
	 */
	protected static  int xCount = Const.levxarray[Const.level];// �������ü���
	/**
	 * yCount y�᷽���ͼ����+2
	 */
	protected static  int yCount = Const.levyarray[Const.level];
	/**
	 * map ��������Ϸ����,map����ӵ�int���ڳ����е���˼��index����������Ļ���꣡
	 */
	protected int[][] map = new int[Const.levxarray[Const.level]][Const.levyarray[Const.level]];
	/**
	 * iconSize ͼ���С��ͼ���������Σ�����һ��int������ʾ����
	 */
	protected int iconSize;
	/**
	 * iconCounts ͼ�����Ŀ
	 */
	protected int iconCounts = 19;
	/**
	 * icons ���е�ͼƬ
	 */
	protected Bitmap[] icons = new Bitmap[iconCounts];
	/**
	 * path ������ͨ���·��
	 */
	private Point[] path = null;
	/**
	 * selected ѡ�е�ͼ��
	 */
	protected List<Point> selected = new ArrayList<Point>();

	/**
	 * ���캯��
	 * 
	 * @param context
	 * @param attrs
	 */
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		calIconSize();
		Resources r = getResources();
		// �����������е�ͼ����Դ
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
	 * ����ͼ��Ĵ�С
	 */
	private void calIconSize() {
		// ȡ����Ļ�Ĵ�С
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		iconSize = dm.widthPixels / (Const.levxarray[Const.level]);
	}

	/**
	 * ����Ŀ����������ͼ����Դ��ͬʱ��һ��key���ض���������ʶ����һ��ͼ����а�
	 * 
	 * @param key
	 *            �ض�ͼ��ı�ʶ
	 * @param d
	 *            drawable�µ���Դ
	 */
	public void loadBitmaps(int key, Drawable d) {
		Bitmap bitmap = Bitmap.createBitmap(iconSize, iconSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, iconSize, iconSize);
		d.draw(canvas);
		icons[key] = bitmap; // δ��0 ��index
	}

	/**
	 * View�Դ��ģ������ڴ˷����У��л�·����ɾ����ͨ������ͼ�꣩�� �������̵�����ͼ�꣨Ҳ�����Ϊˢ�£�ֻҪ��mapλ��ֵ>0��
	 * �Ŵ��һ��ѡ�е�ͼ�꣨selected.size() == 1��
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		/**
		 * ������ͨ·����Ȼ��·���Լ�����ͼ�����
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
		 * �������̵�����ͼ�� ����������ڵ�ֵ����0ʱ����
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
		 * ����ѡ��ͼ�꣬��ѡ��ʱͼ��Ŵ���ʾ
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
	 * ���߷���
	 * 
	 * @param x
	 *            �����еĺ�����
	 * @param y
	 *            �����е�������
	 * @return ��ͼ���������е�����ת������Ļ�ϵ���ʵ����
	 */
	public Point indexToScreen(int x, int y) {
		return new Point(x * iconSize, y * iconSize);
	}

	/**
	 * ���߷���
	 * 
	 * @param x
	 *            ��Ļ�еĺ�����
	 * @param y
	 *            ��Ļ�е�������
	 * @return ��ͼ������Ļ�е�����ת���������ϵ���������
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
	 * ������path���ݸ�����ʾ��Ҳ���ǽ��ܹ����ӵ�ͼ������
	 * 
	 * @param path
	 */
	public void drawLine(Point[] path) {
		this.path = path;
		this.invalidate();
	}

}
