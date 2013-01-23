package com.ooxx.sos;

import android.app.Activity;

import android.widget.Toast;

import com.wogamecenter.api.resource.Achievement;
import com.wogamecenter.api.resource.Leaderboard;
import com.wogamecenter.api.resource.Score;

public class WogameTools {
    static final String LeadboardID_1 = "25"; // ���հ�
    
    static final String[] AchievementID ={
        "41",
        "42",
        "43",
        "44",
        "45",
    }; // �ɾ�
    
    public static void submitScore(final Activity activity,String LeaderboardID, int nScore, String Desc)
    {
//      Score score = new Score(nScore, (Desc.length() > 0 ? Desc : null));
//      Leaderboard lb = new Leaderboard(LeaderboardID);
//      score.submitTo(lb, null);
        //����봦���ؽ�������Դ���ص��࣬�����Ǵ�����ʾ
        Score.SubmitToCB cb = new Score.SubmitToCB()
        {
            @Override
            public void onSuccess(boolean newHighScore)
            {
                if (newHighScore)
                {
                    Toast.makeText(activity, "�ɹ��ϴ����µĵ÷�",
                            Toast.LENGTH_LONG).show();
                } else
                {
                    Toast.makeText(activity, "�ϴ��ķ������Ǹ��ߵķ���",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String exceptionMessage)
            {
                Toast.makeText(activity, "�ϴ�����ʧ��",
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
        
        //����봦���ؽ�������Դ���ص��࣬�����Ǵ�����ʾ
        Achievement.UnlockCB cb = new Achievement.UnlockCB()
        {
            @Override
            public void onSuccess(boolean newUnlock)
            {
                if (newUnlock)
                {
                    
                    Toast.makeText(activity, "���ɹ��Ŀ�����һ���µĳɾ�",
                            Toast.LENGTH_LONG).show();
                } else
                {
                    Toast.makeText(activity, "�óɾ��Ѿ����",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String exceptionMessage)
            {
                Toast.makeText(activity, "�ϴ��ɾ�ʧ��",
                        Toast.LENGTH_LONG).show();
            }
        };
        mAchievement.unlock(cb);
    }

}
