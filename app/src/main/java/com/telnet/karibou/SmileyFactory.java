package com.telnet.karibou;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmileyFactory {
    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, new String[]{":D", ":-D", ":d", ":-D"}, R.drawable.emo_im_youpi);
        addPattern(emoticons, new String[]{":)", ":-)"}, R.drawable.emo_im_happy);
        addPattern(emoticons, new String[]{":(", ":-("}, R.drawable.emo_im_sad);
        addPattern(emoticons, new String[]{";)", ";-)"}, R.drawable.emo_im_wink);
        addPattern(emoticons, new String[]{":-p", ":-P", ":p", ":P"}, R.drawable.emo_im_tongue);
        addPattern(emoticons, new String[]{":'("}, R.drawable.emo_im_cry);
        addPattern(emoticons, new String[]{":o", ":-o", ":O", ":-O"}, R.drawable.emo_im_shocked);
        addPattern(emoticons, new String[]{"[sex]"}, R.drawable.emo_im_sex);
    }

    private static void addPattern(Map<Pattern, Integer> map, String[] smiles,
                                   int resource) {
        for (int i = 0; i < smiles.length; i++) {
            map.put(Pattern.compile(Pattern.quote(smiles[i])), resource);
        }
    }

    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, String text) {
        Spannable spannable = spannableFactory.newSpannable(Html.fromHtml(text));
        addSmiles(context, spannable);
        return spannable;
    }
}
