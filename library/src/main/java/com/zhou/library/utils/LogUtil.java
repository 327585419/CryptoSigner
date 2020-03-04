//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zhou.library.utils;

import android.os.Build.VERSION;
import android.util.Log;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LogUtil {
    private static final LogUtil.Tree[] TREE_ARRAY_EMPTY = new LogUtil.Tree[0];
    private static final List<Tree> FOREST = new ArrayList();
    static volatile LogUtil.Tree[] forestAsArray;
    private static final LogUtil.Tree TREE_OF_SOULS;

    public static void v(@NonNls String message, Object... args) {
        TREE_OF_SOULS.v(message, args);
    }

    public static void v(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.v(t, message, args);
    }

    public static void v(Throwable t) {
        TREE_OF_SOULS.v(t);
    }

    public static void d(@NonNls String message, Object... args) {
        TREE_OF_SOULS.d(message, args);
    }

    public static void d(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.d(t, message, args);
    }

    public static void d(Throwable t) {
        TREE_OF_SOULS.d(t);
    }

    public static void i(@NonNls String message, Object... args) {
        TREE_OF_SOULS.i(message, args);
    }

    public static void i(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.i(t, message, args);
    }

    public static void i(Throwable t) {
        TREE_OF_SOULS.i(t);
    }

    public static void w(@NonNls String message, Object... args) {
        TREE_OF_SOULS.w(message, args);
    }

    public static void w(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.w(t, message, args);
    }

    public static void w(Throwable t) {
        TREE_OF_SOULS.w(t);
    }

    public static void e(@NonNls String message, Object... args) {
        TREE_OF_SOULS.e(message, args);
    }

    public static void e(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.e(t, message, args);
    }

    public static void e(Throwable t) {
        TREE_OF_SOULS.e(t);
    }

    public static void wtf(@NonNls String message, Object... args) {
        TREE_OF_SOULS.wtf(message, args);
    }

    public static void wtf(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.wtf(t, message, args);
    }

    public static void wtf(Throwable t) {
        TREE_OF_SOULS.wtf(t);
    }

    public static void log(int priority, @NonNls String message, Object... args) {
        TREE_OF_SOULS.log(priority, message, args);
    }

    public static void log(int priority, Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.log(priority, t, message, args);
    }

    public static void log(int priority, Throwable t) {
        TREE_OF_SOULS.log(priority, t);
    }

    @NotNull
    public static LogUtil.Tree asTree() {
        return TREE_OF_SOULS;
    }

    @NotNull
    public static LogUtil.Tree tag(String tag) {
        LogUtil.Tree[] forest = forestAsArray;
        int i = 0;

        for(int count = forest.length; i < count; ++i) {
            forest[i].explicitTag.set(tag);
        }

        return TREE_OF_SOULS;
    }

    public static void plant(@NotNull LogUtil.Tree tree) {
        if (tree == TREE_OF_SOULS) {
            throw new IllegalArgumentException("Cannot plant LogUtil into itself.");
        } else {
            synchronized(FOREST) {
                FOREST.add(tree);
                forestAsArray = (LogUtil.Tree[])FOREST.toArray(new LogUtil.Tree[FOREST.size()]);
            }
        }
    }

    public static void plant(@NotNull LogUtil.Tree... trees) {
        LogUtil.Tree[] var1 = trees;
        int var2 = trees.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            LogUtil.Tree tree = var1[var3];
            if (tree == null) {
                throw new NullPointerException("trees contains null");
            }

            if (tree == TREE_OF_SOULS) {
                throw new IllegalArgumentException("Cannot plant LogUtil into itself.");
            }
        }

        synchronized(FOREST) {
            Collections.addAll(FOREST, trees);
            forestAsArray = (LogUtil.Tree[])FOREST.toArray(new LogUtil.Tree[FOREST.size()]);
        }
    }

    public static void uproot(@NotNull LogUtil.Tree tree) {
        synchronized(FOREST) {
            if (!FOREST.remove(tree)) {
                throw new IllegalArgumentException("Cannot uproot tree which is not planted: " + tree);
            } else {
                forestAsArray = (LogUtil.Tree[])FOREST.toArray(new LogUtil.Tree[FOREST.size()]);
            }
        }
    }

    public static void uprootAll() {
        synchronized(FOREST) {
            FOREST.clear();
            forestAsArray = TREE_ARRAY_EMPTY;
        }
    }

    @NotNull
    public static List<Tree> forest() {
        synchronized(FOREST) {
            return Collections.unmodifiableList(new ArrayList(FOREST));
        }
    }

    public static int treeCount() {
        synchronized(FOREST) {
            return FOREST.size();
        }
    }

    private LogUtil() {
        throw new AssertionError("No instances.");
    }

    static {
        forestAsArray = TREE_ARRAY_EMPTY;
        TREE_OF_SOULS = new LogUtil.Tree() {
            public void v(String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].v(message, args);
                }

            }

            public void v(Throwable t, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].v(t, message, args);
                }

            }

            public void v(Throwable t) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].v(t);
                }

            }

            public void d(String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].d(message, args);
                }

            }

            public void d(Throwable t, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].d(t, message, args);
                }

            }

            public void d(Throwable t) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].d(t);
                }

            }

            public void i(String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].i(message, args);
                }

            }

            public void i(Throwable t, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].i(t, message, args);
                }

            }

            public void i(Throwable t) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].i(t);
                }

            }

            public void w(String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].w(message, args);
                }

            }

            public void w(Throwable t, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].w(t, message, args);
                }

            }

            public void w(Throwable t) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].w(t);
                }

            }

            public void e(String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].e(message, args);
                }

            }

            public void e(Throwable t, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].e(t, message, args);
                }

            }

            public void e(Throwable t) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].e(t);
                }

            }

            public void wtf(String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].wtf(message, args);
                }

            }

            public void wtf(Throwable t, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].wtf(t, message, args);
                }

            }

            public void wtf(Throwable t) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].wtf(t);
                }

            }

            public void log(int priority, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].log(priority, message, args);
                }

            }

            public void log(int priority, Throwable t, String message, Object... args) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].log(priority, t, message, args);
                }

            }

            public void log(int priority, Throwable t) {
                LogUtil.Tree[] forest = LogUtil.forestAsArray;
                int i = 0;

                for(int count = forest.length; i < count; ++i) {
                    forest[i].log(priority, t);
                }

            }

            protected void log(int priority, String tag, @NotNull String message, Throwable t) {
                throw new AssertionError("Missing override for log method.");
            }
        };
    }

    public static class DebugTree extends LogUtil.Tree {
        private static final int MAX_LOG_LENGTH = 4000;
        private static final int MAX_TAG_LENGTH = 23;
        private static final int CALL_STACK_INDEX = 5;
        private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

        public DebugTree() {
        }

        @Nullable
        protected String createStackElementTag(@NotNull StackTraceElement element) {
            String tag = element.getClassName();
            Matcher m = ANONYMOUS_CLASS.matcher(tag);
            if (m.find()) {
                tag = m.replaceAll("");
            }

            tag = tag.substring(tag.lastIndexOf(46) + 1);
            return tag.length() > 23 && VERSION.SDK_INT < 24 ? tag.substring(0, 23) : tag;
        }

        final String getTag() {
            String tag = super.getTag();
            if (tag != null) {
                return tag;
            } else {
                StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
                if (stackTrace.length <= 5) {
                    throw new IllegalStateException("Synthetic stacktrace didn't have enough elements: are you using proguard?");
                } else {
                    return this.createStackElementTag(stackTrace[5]);
                }
            }
        }

        protected void log(int priority, String tag, @NotNull String message, Throwable t) {
            if (message.length() < 4000) {
                if (priority == 7) {
                    Log.wtf(tag, message);
                } else {
                    Log.println(priority, tag, message);
                }

            } else {
                int i = 0;

                int end;
                for(int length = message.length(); i < length; i = end + 1) {
                    int newline = message.indexOf(10, i);
                    newline = newline != -1 ? newline : length;

                    do {
                        end = Math.min(newline, i + 4000);
                        String part = message.substring(i, end);
                        if (priority == 7) {
                            Log.wtf(tag, part);
                        } else {
                            Log.println(priority, tag, part);
                        }

                        i = end;
                    } while(end < newline);
                }

            }
        }
    }

    public abstract static class Tree {
        final ThreadLocal<String> explicitTag = new ThreadLocal();

        public Tree() {
        }

        @Nullable
        String getTag() {
            String tag = (String)this.explicitTag.get();
            if (tag != null) {
                this.explicitTag.remove();
            }

            return tag;
        }

        public void v(String message, Object... args) {
            this.prepareLog(2, (Throwable)null, message, args);
        }

        public void v(Throwable t, String message, Object... args) {
            this.prepareLog(2, t, message, args);
        }

        public void v(Throwable t) {
            this.prepareLog(2, t, (String)null);
        }

        public void d(String message, Object... args) {
            this.prepareLog(3, (Throwable)null, message, args);
        }

        public void d(Throwable t, String message, Object... args) {
            this.prepareLog(3, t, message, args);
        }

        public void d(Throwable t) {
            this.prepareLog(3, t, (String)null);
        }

        public void i(String message, Object... args) {
            this.prepareLog(4, (Throwable)null, message, args);
        }

        public void i(Throwable t, String message, Object... args) {
            this.prepareLog(4, t, message, args);
        }

        public void i(Throwable t) {
            this.prepareLog(4, t, (String)null);
        }

        public void w(String message, Object... args) {
            this.prepareLog(5, (Throwable)null, message, args);
        }

        public void w(Throwable t, String message, Object... args) {
            this.prepareLog(5, t, message, args);
        }

        public void w(Throwable t) {
            this.prepareLog(5, t, (String)null);
        }

        public void e(String message, Object... args) {
            this.prepareLog(6, (Throwable)null, message, args);
        }

        public void e(Throwable t, String message, Object... args) {
            this.prepareLog(6, t, message, args);
        }

        public void e(Throwable t) {
            this.prepareLog(6, t, (String)null);
        }

        public void wtf(String message, Object... args) {
            this.prepareLog(7, (Throwable)null, message, args);
        }

        public void wtf(Throwable t, String message, Object... args) {
            this.prepareLog(7, t, message, args);
        }

        public void wtf(Throwable t) {
            this.prepareLog(7, t, (String)null);
        }

        public void log(int priority, String message, Object... args) {
            this.prepareLog(priority, (Throwable)null, message, args);
        }

        public void log(int priority, Throwable t, String message, Object... args) {
            this.prepareLog(priority, t, message, args);
        }

        public void log(int priority, Throwable t) {
            this.prepareLog(priority, t, (String)null);
        }

        /** @deprecated */
        @Deprecated
        protected boolean isLoggable(int priority) {
            return true;
        }

        protected boolean isLoggable(@Nullable String tag, int priority) {
            return this.isLoggable(priority);
        }

        private void prepareLog(int priority, Throwable t, String message, Object... args) {
            String tag = this.getTag();
            if (this.isLoggable(tag, priority)) {
                if (message != null && message.length() == 0) {
                    message = null;
                }

                if (message == null) {
                    if (t == null) {
                        return;
                    }

                    message = this.getStackTraceString(t);
                } else {
                    if (args != null && args.length > 0) {
                        message = this.formatMessage(message, args);
                    }

                    if (t != null) {
                        message = message + "\n" + this.getStackTraceString(t);
                    }
                }

                this.log(priority, tag, message, t);
            }
        }

        protected String formatMessage(@NotNull String message, @NotNull Object[] args) {
            return String.format(message, args);
        }

        private String getStackTraceString(Throwable t) {
            StringWriter sw = new StringWriter(256);
            PrintWriter pw = new PrintWriter(sw, false);
            t.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }

        protected abstract void log(int var1, @Nullable String var2, @NotNull String var3, @Nullable Throwable var4);
    }
}
