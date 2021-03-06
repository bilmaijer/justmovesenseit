// Generated code from Butter Knife. Do not modify!
package com.movesense.mds.sampleapp.example_app_using_mds_api.mainView;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.movesense.mds.sampleapp.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainViewActivity_ViewBinding implements Unbinder {
  private MainViewActivity target;

  private View view2131230839;

  private View view2131230840;

  private View view2131230837;

  private View view2131230841;

  private View view2131230842;

  private View view2131230835;

  @UiThread
  public MainViewActivity_ViewBinding(MainViewActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainViewActivity_ViewBinding(final MainViewActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.mainView_movesense_Ll, "field 'mMainViewMovesenseLl' and method 'onViewClicked'");
    target.mMainViewMovesenseLl = Utils.castView(view, R.id.mainView_movesense_Ll, "field 'mMainViewMovesenseLl'", LinearLayout.class);
    view2131230839 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.mainView_multiConnection_Ll, "field 'mMainViewMultiConnectionLl' and method 'onViewClicked'");
    target.mMainViewMultiConnectionLl = Utils.castView(view, R.id.mainView_multiConnection_Ll, "field 'mMainViewMultiConnectionLl'", LinearLayout.class);
    view2131230840 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.mainView_dfu_Ll, "field 'mMainViewDfuLl' and method 'onViewClicked'");
    target.mMainViewDfuLl = Utils.castView(view, R.id.mainView_dfu_Ll, "field 'mMainViewDfuLl'", LinearLayout.class);
    view2131230837 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.mainView_savedData_Ll, "field 'mMainViewSavedDataLl' and method 'onViewClicked'");
    target.mMainViewSavedDataLl = Utils.castView(view, R.id.mainView_savedData_Ll, "field 'mMainViewSavedDataLl'", LinearLayout.class);
    view2131230841 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.mainView_tests_Ll, "field 'mMainViewTestsLl' and method 'onViewClicked'");
    target.mMainViewTestsLl = Utils.castView(view, R.id.mainView_tests_Ll, "field 'mMainViewTestsLl'", LinearLayout.class);
    view2131230842 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.mainView_about_Ll, "field 'mMainViewAboutLl' and method 'onViewClicked'");
    target.mMainViewAboutLl = Utils.castView(view, R.id.mainView_about_Ll, "field 'mMainViewAboutLl'", LinearLayout.class);
    view2131230835 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mMainViewAppVersionTv = Utils.findRequiredViewAsType(source, R.id.mainView_appVersion_tv, "field 'mMainViewAppVersionTv'", TextView.class);
    target.mMainViewLibraryVersionTv = Utils.findRequiredViewAsType(source, R.id.mainView_libraryVersion_tv, "field 'mMainViewLibraryVersionTv'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainViewActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mMainViewMovesenseLl = null;
    target.mMainViewMultiConnectionLl = null;
    target.mMainViewDfuLl = null;
    target.mMainViewSavedDataLl = null;
    target.mMainViewTestsLl = null;
    target.mMainViewAboutLl = null;
    target.mMainViewAppVersionTv = null;
    target.mMainViewLibraryVersionTv = null;

    view2131230839.setOnClickListener(null);
    view2131230839 = null;
    view2131230840.setOnClickListener(null);
    view2131230840 = null;
    view2131230837.setOnClickListener(null);
    view2131230837 = null;
    view2131230841.setOnClickListener(null);
    view2131230841 = null;
    view2131230842.setOnClickListener(null);
    view2131230842 = null;
    view2131230835.setOnClickListener(null);
    view2131230835 = null;
  }
}
