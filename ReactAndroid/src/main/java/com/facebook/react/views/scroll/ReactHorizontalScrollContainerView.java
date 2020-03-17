// Copyright (c) Facebook, Inc. and its affiliates.

// This source code is licensed under the MIT license found in the
// LICENSE file in the root directory of this source tree.

package com.facebook.react.views.scroll;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import androidx.core.view.ViewCompat;
import com.facebook.react.modules.i18nmanager.I18nUtil;

/** Container of Horizontal scrollViews that supports RTL scrolling. */
public class ReactHorizontalScrollContainerView extends ViewGroup {

  private int mLayoutDirection;
//  private int mCurrentWidth;
  private int mLastWidth = 0;
  private Listener rtlListener = null;

  public interface Listener {
    void onLayout();
  }

  public ReactHorizontalScrollContainerView(Context context) {
    super(context);
    mLayoutDirection =
        I18nUtil.getInstance().isRTL(context)
            ? ViewCompat.LAYOUT_DIRECTION_RTL
            : ViewCompat.LAYOUT_DIRECTION_LTR;
//    mCurrentWidth = 0;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    // This is to fix the overflowing (scaled) item being cropped
    final HorizontalScrollView parent = (HorizontalScrollView) getParent();
    parent.setClipChildren(false);
    this.setClipChildren(false);

    if (mLayoutDirection == LAYOUT_DIRECTION_RTL) {
      // When the layout direction is RTL, we expect Yoga to give us a layout
      // that extends off the screen to the left so we re-center it with left=0
      int newLeft = 0;
      int width = right - left;
      int newRight = newLeft + width;
      setLeft(newLeft);
      setRight(newRight);

      // Call with the present values in order to re-layout if necessary
//      HorizontalScrollView parentScrollView = (HorizontalScrollView) getParent();
      // Fix the ScrollX position when using RTL language
//      int offsetX = parentScrollView.getScrollX() + getWidth() - mCurrentWidth;
//      parentScrollView.scrollTo(offsetX, parentScrollView.getScrollY());

      // Fix the ScrollX position when using RTL language accounting for the case when new
      // data is appended to the "end" (left) of the view (e.g. after fetching additional items)
      final int offsetX = this.getMeasuredWidth() - mLastWidth + parent.getScrollX();

      // Call with the present values in order to re-layout if necessary
      parent.scrollTo(offsetX, parent.getScrollY());
      mLastWidth = this.getMeasuredWidth();

      // Use the listener to adjust the scrollposition if new data was appended
      if (rtlListener != null) {
        rtlListener.onLayout();
      }
    }
//    mCurrentWidth = getWidth();
  }
  public int getLastWidth() {
    return mLastWidth;
  }

  public void setListener(Listener rtlListener) {
    this.rtlListener = rtlListener;
  }
}
