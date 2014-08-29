package app.sunshine.juanjo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by juanjo on 28/08/14.
 */
public class MyView extends View {
	public MyView(Context context) {
		super(context);
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
	}

	protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {

	}
}
