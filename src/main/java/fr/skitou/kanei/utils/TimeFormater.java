package fr.skitou.kanei.utils;

public class TimeFormater {

    public static long formatedDurationToMilis(String formatedDuration) {
        String[] splited = formatedDuration.split(":");
        long hours = 0;
        long minutes = 0;
        long seconds = 0;

        switch (splited.length) {
            case 1 -> seconds = Long.parseLong(splited[0]);
            case 2 -> {
                minutes = Long.parseLong(splited[0]);
                seconds = Long.parseLong(splited[1]);
            }
            case 3 -> {
                hours = Long.parseLong(splited[0]);
                minutes = Long.parseLong(splited[1]);
                seconds = Long.parseLong(splited[2]);
            }
        }

        return (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000);
    }

    public static String milisToFormatedDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds).concat("s");
        }
    }
}
