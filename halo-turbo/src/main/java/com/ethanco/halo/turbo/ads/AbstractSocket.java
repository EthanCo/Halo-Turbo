package com.ethanco.halo.turbo.ads;

import android.os.Handler;
import android.os.Looper;

import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.impl.convert.ConvertManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class AbstractSocket implements ISocket, ILog {
    protected Config config;
    protected ISession session = null;
    protected List<IHandler> handlers = new ArrayList<>();
    protected Handler M = new Handler(Looper.getMainLooper());
    protected ConvertManager convertManager;

    public AbstractSocket(Config config) {
        this.config = config;
        convertManager = new ConvertManager(config.convertors);
        addAllHandler();
        getHandlers();
    }

    @Override
    public boolean start() {
        onStarting();
        return false;
    }

    @Override
    public void stop() {
        onStopping();
        M.removeCallbacksAndMessages(null);
    }

    private void addAllHandler() {
        if (config.handlers != null) {
            for (IHandler handler : config.handlers) {
                if (handler instanceof AbstractLog) {
                    AbstractLog logHandler = (AbstractLog) (handler);
                    logHandler.setPrefix(getSimpleName());
                }
            }

            handlers.addAll(config.handlers);
        }
    }

    @Override
    public List<IHandler> getHandlers() {
        return handlers;
    }

    @Override
    public void addHandler(IHandler handler) {
        if (handler == null) return;

        if (!handlers.contains(handler)) {
            handlers.add(handler);
        }
    }

    @Override
    public boolean removeHandler(IHandler handler) {
        return handlers.remove(handler);
    }

    protected String getSimpleName() {
        return getClass().getSimpleName();
    }

    public Object convert(Object message) {
        if (convertManager != null) {
            Object convertData = convertManager.convert(message);
            //Log.i("Z-Test", "convert" + convertData);
            return convertData;
        }
        return message;
    }

    protected Object receive(Object message) {
        if (convertManager != null) {
            return convertManager.receive(message);
        }

        return message;
    }

    @Override
    public void sessionCreated(final ISession session) {
        M.post(new Runnable() {
            @Override
            public void run() {
                for (IHandler handler : handlers) {
                    if (handler != null) {
                        handler.sessionCreated(session);
                    }
                }
            }
        });
    }

    @Override
    public void sessionOpened(final ISession session) {
        M.post(new Runnable() {
            @Override
            public void run() {
                for (IHandler handler : handlers) {
                    if (handler != null) {
                        handler.sessionOpened(session);
                    }
                }
            }
        });
    }

    @Override
    public void sessionClosed(final ISession session) {
        M.post(new Runnable() {
            @Override
            public void run() {
                for (IHandler handler : handlers) {
                    if (handler != null) {
                        handler.sessionClosed(session);
                    }
                }
            }
        });
    }

    @Override
    public void messageReceived(final ISession session, final Object message) {
        M.post(new Runnable() {
            @Override
            public void run() {
                for (IHandler handler : handlers) {
                    if (handler != null) {
                        handler.messageReceived(session, message);
                    }
                }
            }
        });
    }

    @Override
    public void messageSent(final ISession session, final Object message) {
        M.post(new Runnable() {
            @Override
            public void run() {
                for (IHandler handler : handlers) {
                    if (handler != null) {
                        handler.messageSent(session, message);
                    }
                }
            }
        });
    }

    @Override
    public void onStarting() {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onStarting();
            }
        });
    }

    @Override
    public void onStartSuccess() {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onStartSuccess();
            }
        });
    }

    @Override
    public void onStartFailed(final Exception e) {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onStartFailed(e);
            }
        });
    }

    @Override
    public void onStopping() {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onStopping();
            }
        });
    }

    @Override
    public void onStopped() {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onStopped();
            }
        });
    }

    @Override
    public void onReceiveException(final Exception e) {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onReceiveException(e);
            }
        });
    }

    @Override
    public void onReConnecting() {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onReConnecting();
            }
        });
    }

    @Override
    public void onReConnected() {
        execLog(new LogListener() {
            @Override
            public void onExec(ILog log) {
                log.onReConnected();
            }
        });
    }

    public interface LogListener {
        void onExec(ILog log);
    }

    public void execLog(final LogListener logListener) {
        M.post(new Runnable() {
            @Override
            public void run() {
                for (IHandler handler : handlers) {
                    if (handler instanceof ILog) {
                        ILog log = (ILog) handler;
                        logListener.onExec(log);
                    }
                }
            }
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
