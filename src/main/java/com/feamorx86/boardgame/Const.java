package com.feamorx86.boardgame;

/**
 * Created by Home on 25.08.2017.
 */
public class Const {
    public static class UserTypes {
        public static final int STUDENT = 0;
        public static final int TEACHER = 1;
        public static final int ADMIN = 2;
    }

    public static class TextDataTypes {
        public static final int SIMPLE_TEXT = 0;
        public static final int SIMPLE_IMAGE = 1;
        public static final int WEB_VIEW = 2;

        public static String[] listNames() {
            return new String[]{"Обычный текст", "Одна картинка", "HTML"};
        }

        public static String[] listValues() {
            return new String[]{Integer.toString(SIMPLE_TEXT), Integer.toString(SIMPLE_IMAGE), Integer.toString(WEB_VIEW)};
        }
    }

    public static class QuestionTypes {
        public static final int ONE_CORRECT = 0;
        public static final int MANY_CORRECT = 1;
        public static final int INT_INPUT = 2;
        public static final int FLOAT_INPUT = 3;
        public static final int TEXT_INPUT = 4;
    }

    public static class AppInfo {
        public static final int MAJOR_VERSION = 0;
        public static final int MINOR_VERSION = 3;
        public static final int BUILD = 1;

        public static final String OWNER_EMAIL = "feamorx86@yandex.ru";
        public static final String APP_NAME = "BoardGame";

        public static String getVersion() {
            return Integer.toString(MAJOR_VERSION)+"."+Integer.toString(MINOR_VERSION)+"."+Integer.toString(BUILD);
        }
    }

    public static class GameTypes {
        public static final int TYPE_CHAT = 100;
        public static final int GORODA_REKI_SELA = 200;
    }

}
