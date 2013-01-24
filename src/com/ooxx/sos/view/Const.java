package com.ooxx.sos.view;

import android.R.integer;

import com.ooxx.sos.R;

public class Const {
	public static boolean soundon = true;
	/**
	 * 对话框显示次数
	 */
	public static int count = 0;
	/**
	 * 是否第一次启动
	 */
	public static boolean first = true;
	/**
	 * 是否成功进入下一级
	 */
	public static boolean issuccess = false;
	/**
	 * 游戏级数
	 */
	public static int level = 0;
	/**
	 * 总关卡数
	 */
	public static int totallevels = 15;
	/**
	 * 游戏是否暂停
	 */
	public static boolean ispause = false;
	/**
	 * 是否开始下一级
	 */
	public static boolean next = false;
	/**
	 * 总时间
	 */
	public static int totalTime = 100;
	/**
	 * 提示帮助的次数
	 */
	public static int refresh[] = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
	/**
	 * 用于刷新的次数
	 */
	public static int help[] = { 3, 3, 3, 5, 5, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8 };
	/**
	 * xCount x轴方向的图标数-2
	 */
	public static int xCount = 6;
	/**
	 * yCount y轴方向的图表数-2
	 */
	public static int yCount = 8;
	/**
	 * 代表翻到第几页
	 */
	public static int page = 1;
	/**
	 * 代表一共多少页
	 */
	public static int sum;
	/**
	 * 级别 五个级别
	 */
	public static int levxarray[] = { 6, 6, 6, 7, 8, 8, 8, 9, 10, 10, 10, 11,
			11, 12, 12 };
	/**
	 * 级别
	 */
	public static int levyarray[] = { 6, 7, 8, 8, 8, 9, 10, 10, 10, 11, 12, 12,
			14, 14, 15 };
	/**
	 * 4*4 4*5 4*6 5*6 6*6 6*7 6*8 7*8 8*8 8*9 8*10 9*10 9*12 10*12 10*13
	 */
	public static int[] pic[] = {// 480*720 不能大于720貌似
			{ R.drawable.pre0, R.drawable.pre1, R.drawable.pre2,
					R.drawable.pre3, R.drawable.pre4 },
			{ R.drawable.pre5, R.drawable.pre6, R.drawable.pre7,
					R.drawable.pre8, R.drawable.pre9 },
			{ R.drawable.pre10, R.drawable.pre11, R.drawable.pre12,
					R.drawable.pre13, R.drawable.pre14 },
			{ R.drawable.pre15, R.drawable.pre16, R.drawable.pre17,
					R.drawable.pre18, R.drawable.pre19 },
			{ R.drawable.pre20, R.drawable.pre21, R.drawable.pre22,
					R.drawable.pre23, R.drawable.pre24 },
			{ R.drawable.pre25, R.drawable.pre26, R.drawable.pre27,
					R.drawable.pre28, R.drawable.pre29 },
			{ R.drawable.pre30, R.drawable.pre31, R.drawable.pre32,
					R.drawable.pre33, R.drawable.pre34 },
			{ R.drawable.pre35, R.drawable.pre36, R.drawable.pre37,
					R.drawable.pre38, R.drawable.pre39 },
			{ R.drawable.pre40, R.drawable.pre41, R.drawable.pre42,
					R.drawable.pre43, R.drawable.pre44 },
			{ R.drawable.pre45, R.drawable.pre46, R.drawable.pre47,
					R.drawable.pre48, R.drawable.pre49 },
			{ R.drawable.pre50, R.drawable.pre51, R.drawable.pre52,
					R.drawable.pre53, R.drawable.pre54 },
			{ R.drawable.pre55, R.drawable.pre56, R.drawable.pre57,
					R.drawable.pre58, R.drawable.pre59 },
			{ R.drawable.pre60, R.drawable.pre61, R.drawable.pre62,
					R.drawable.pre63, R.drawable.pre64 },
			{ R.drawable.pre65, R.drawable.pre66, R.drawable.pre67,
					R.drawable.pre68, R.drawable.pre69 },
			{ R.drawable.pre70, R.drawable.pre71, R.drawable.pre72,
					R.drawable.pre73, R.drawable.pre74 } };
}
