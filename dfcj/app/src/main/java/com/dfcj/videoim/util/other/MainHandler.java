package com.dfcj.videoim.util.other;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class MainHandler {

    private final static ArrayList<Runnable> list = new ArrayList<>();
    private final static Object lock = new Object();

    private final Handler handler;

    private MainHandler(@NonNull Looper looper) {
        handler = new Handler(looper);
    }

    private static MainHandler myHandler;

    public static MainHandler getInstance() {
        if (myHandler == null) {
            synchronized (MainHandler.class) {
                if (myHandler == null) {
                    myHandler = new MainHandler(Looper.getMainLooper());
                }
            }
        }
        return myHandler;
    }

    public boolean post(@NonNull Runnable runnable) {
        return postDelayed(runnable, 0);
    }

    public boolean postDelayed(@NonNull Runnable runnable, long delayMillis) {
        WeakRunnable weakRunnable = new WeakRunnable(runnable);
        synchronized (lock) {
            list.add(weakRunnable);
        }
        return handler.postDelayed(weakRunnable, delayMillis);
    }

    public boolean post(ILive iLive, @NonNull Runnable runnable) {
        return postDelayed(iLive, runnable, 0);
    }

    public boolean postDelayed(ILive iLive, @NonNull Runnable runnable, long delayMillis) {
        LiveRunnable liveRunnable = new LiveRunnable(iLive, runnable);
        synchronized (lock) {
            list.add(liveRunnable);
        }
        return handler.postDelayed(liveRunnable, delayMillis);
    }

    public void remove(Runnable runnable) {
        synchronized (lock) {
            Iterator<Runnable> iterator = list.iterator();
            while (iterator.hasNext()) {
                Runnable temp = iterator.next();
                if (temp instanceof WeakRunnable) {
                    Runnable value = ((WeakRunnable) temp).get();
                    if (value == runnable) {
                        iterator.remove();
                        handler.removeCallbacks(temp);
                    }
                } else if (temp instanceof LiveRunnable) {
                    Runnable value = ((LiveRunnable) temp).runnable.get();
                    if (value == runnable) {
                        iterator.remove();
                        handler.removeCallbacks(temp);
                    }
                }
            }
        }
    }

    public boolean sendMessage(@NonNull Message message, Callback callback) {
        return sendMessageDelayed(message, 0, callback);
    }

    public boolean sendMessageDelayed(@NonNull Message message, long delayMillis, Callback callback) {
        CallbackMessageObject runnable = new CallbackMessageObject(message, callback);
        synchronized (lock) {
            list.add(runnable);
        }
        return handler.postDelayed(runnable, delayMillis);
    }

    public void removeMessage(int what) {
        synchronized (lock) {
            Iterator<Runnable> iterator = list.iterator();
            while (iterator.hasNext()) {
                Runnable value = iterator.next();
                if (value instanceof CallbackMessageObject) {
                    Message message = ((CallbackMessageObject) value).first;
                    if (message != null && message.what == what) {
                        handler.removeCallbacks(value);
                        iterator.remove();
                    }
                }
            }
        }
    }


    public void removeMessage(Object obj) {
        synchronized (lock) {
            Iterator<Runnable> iterator = list.iterator();
            while (iterator.hasNext()) {
                Runnable value = iterator.next();
                if (value instanceof CallbackMessageObject) {
                    Message message = ((CallbackMessageObject) value).first;
                    if (message != null && message.obj == obj) {
                        handler.removeCallbacks(value);
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static class WeakRunnable extends WeakReference<Runnable> implements Runnable {
        public WeakRunnable(Runnable runnable) {
            super(runnable);
        }

        @Override
        public void run() {
            Runnable runnable = get();
            if (runnable != null) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    synchronized (lock) {
                        list.remove(this);
                    }
                }
            }
        }
    }

    private static class LiveRunnable implements Runnable {
        private final WeakReference<ILive> iLiveWeakReference;
        private final WeakReference<Runnable> runnable;

        public LiveRunnable(ILive context, Runnable runnable) {
            this.iLiveWeakReference = new WeakReference<>(context);
            this.runnable = new WeakReference<>(runnable);
        }

        @Override
        public void run() {
            ILive iLive = this.iLiveWeakReference.get();
            Runnable runnable = this.runnable.get();
            if (runnable != null) {
                try {
                    if (iLive != null && iLive.isAlive()) {
                        runnable.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    synchronized (lock) {
                        list.remove(this);
                    }
                }
            }
        }
    }

    private static class CallbackMessageObject extends Pair<Message, WeakReference<Callback>> implements Runnable {

        public CallbackMessageObject(Message first, Callback second) {
            super(first, new WeakReference<>(second));
        }

        @Override
        public void run() {
            Callback callback = second.get();
            if (first != null) {
                try {
                    if (callback != null) {
                        callback.handleMessage(first);
                    }
                    first.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    synchronized (lock) {
                        list.remove(this);
                    }
                }
            }
        }
    }

    public interface Callback {
        void handleMessage(Message message);
    }

    public interface ILive {
        boolean isAlive();
    }

}
