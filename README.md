# android-util2
this a second util lib of  android.


# Gradle config

```java
  compile 'com.heaven7.android.util2:android-util2:<see release>'
```

# sample 

Toast
```java
public class ToastTestActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_toast;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {

    }

    @OnClick(R.id.bt_toast_normal)
    public void onClickNormalToast(View v){
        getToastWindow().type(IWindow.TYPE_NORMAL).show("your toast message");
    }
    @OnClick(R.id.bt_toast_warn)
    public void onClickWarnToast(final View v){
        getToastWindow()
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.show(v.getContext(), "action end...");
                    }
                })
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.show(v.getContext(), "action start...");
                    }
                })
                .type(IWindow.TYPE_WARN)
                .show("your toast message");
    }
    @OnClick(R.id.bt_toast_error)
    public void onClickErrorToast(View v){
        getToastWindow().type(IWindow.TYPE_ERROR).show("your toast message");
    }
    @OnClick(R.id.bt_toast_click)
    public void onClickClickToast(View v){
        final View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.show(v.getContext(), "Toast view was clicked.");
            }
        };
        getToastWindow()
                .type(IWindow.TYPE_WARN)
                .enableClick(true)
                .bindView(new IWindow.IViewBinder() {
                    @Override
                    public void onBind(View view) {
                        view.setOnClickListener(l);
                    }
                })
                .show("your toast message");
    }
}
```
