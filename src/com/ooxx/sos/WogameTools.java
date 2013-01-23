package com.ooxx.sos;

import android.app.Activity;

import android.widget.Toast;

import com.wogamecenter.api.resource.Achievement;
import com.wogamecenter.api.resource.Leaderboard;
import com.wogamecenter.api.resource.Score;

public class WogameTools {
    static final String LeadboardID_1 = "25"; // 最终榜
    
    static final String[] AchievementID ={
        "41",
        "42",
        "43",
        "44",
        "45",
    }; // 成就
    
    public static void submitScore(final Activity activity,String LeaderboardID, int nScore, String Desc)
    {
//      Score score = new Score(nScore, (Desc.length() > 0 ? Desc : null));
//      Leaderboard lb = new Leaderboard(LeaderboardID);
//      score.submitTo(lb, null);
        //如果想处理返回结果，可以传入回调类，下面是代码演示
        Score.SubmitToCB cb = new Score.SubmitToCB()
        {
            @Override
            public void onSuccess(boolean newHighScore)
            {
                if (newHighScore)
                {
                    Toast.makeText(activity, "成功上传了新的得分",
                            Toast.LENGTH_LONG).show();
                } else
                {
                    Toast.makeText(activity, "上传的分数不是更高的分数",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String exceptionMessage)
            {
                Toast.makeText(activity, "上传分数失败",
                        Toast.LENGTH_LONG).show();
            }
        };
        Score score = new Score(nScore, (Desc.length() > 0 ? Desc : null));
        Leaderboard lb = new Leaderboard(LeaderboardID);
        score.submitTo(lb, cb);
    }
    
    public static  void UnlockAchievement(final Activity activity,String AchievementID)
    {
        Achievement mAchievement = new Achievement(AchievementID);
//        mAchievement.unlock(null);
        
        //如果想处理返回结果，可以传入回调类，下面是代码演示
        Achievement.UnlockCB cb = new Achievement.UnlockCB()
        {
            @Override
            public void onSuccess(boolean newUnlock)
            {
                if (newUnlock)
                {
                    
                    Toast.makeText(activity, "您成功的开启了一个新的成就",
                            Toast.LENGTH_LONG).show();
                } else
                {
                    Toast.makeText(activity, "该成就已经完成",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String exceptionMessage)
            {
                Toast.makeText(activity, "上传成就失败",
                        Toast.LENGTH_LONG).show();
            }
        };
        mAchievement.unlock(cb);
    }

}
