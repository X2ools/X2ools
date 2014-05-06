
package org.x2ools.t9apps.match;

import android.annotation.SuppressLint;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToPinYinUtils {

    public static List<String> getPinyinList(List<String> list) {
        List<String> pinyinList = new ArrayList<String>();
        for (Iterator<String> i = list.iterator(); i.hasNext();) {
            String str = (String) i.next();
            try {
                String pinyin = getPinYin(str);
                pinyinList.add(pinyin);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return pinyinList;
    }

    public static String getPinYin(String zhongwen)
            throws BadHanyuPinyinOutputFormatCombination {

        String zhongWenPinYin = "";
        char[] chars = zhongwen.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i],
                    getDefaultOutputFormat());
            if (pinYin != null) {
                zhongWenPinYin += pinYin[0];
            } else {
                zhongWenPinYin += chars[i];
            }
        }
        return zhongWenPinYin;
    }

    /**
     * ������?
     * 
     * @return
     */
    private static HanyuPinyinOutputFormat getDefaultOutputFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);// ��д
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// û����������
        format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);// u��ʾ
        return format;
    }

    @SuppressLint("DefaultLocale")
    public static byte[] getPinyinNum(String name) {
        try {
            if (name != null && name.length() != 0) {
                int len = name.length();
                byte[] nums = new byte[len];
                for (int i = 0; i < len; i++) {
                    String tmp = name.substring(i);
                    nums[i] = getOneNumFromAlpha(ToPinYinUtils.getPinYin(tmp)
                            .toLowerCase().charAt(0));
                }
                return nums;
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte getOneNumFromAlpha(char firstAlpha) {
        switch (firstAlpha) {
            case 'a':
            case 'b':
            case 'c':
                return 2;
            case 'd':
            case 'e':
            case 'f':
                return 3;
            case 'g':
            case 'h':
            case 'i':
                return 4;
            case 'j':
            case 'k':
            case 'l':
                return 5;
            case 'm':
            case 'n':
            case 'o':
                return 6;
            case 'p':
            case 'q':
            case 'r':
            case 's':
                return 7;
            case 't':
            case 'u':
            case 'v':
                return 8;
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                return 9;
            default:
                return 0;
        }
    }

}
