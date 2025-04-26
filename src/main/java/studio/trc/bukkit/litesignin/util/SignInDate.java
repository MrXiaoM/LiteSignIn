package studio.trc.bukkit.litesignin.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Range;

@SuppressWarnings("MagicConstant")
public class SignInDate
    implements Serializable
{
    @Getter
    @Setter
    private int year;
    @Getter
    @Setter
    @Range(from=1, to=12)
    private int month;
    @Getter
    @Setter
    private int day;
    @Getter
    @Setter
    private int hour;
    @Getter
    @Setter
    private int minute;
    @Getter
    @Setter
    private int second;
    @Setter
    private boolean timePeriodFound = false;
    
    public SignInDate(Date d) {
        String str = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(d);
        String[] date = str.split("-");
        
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);

        check(year, month, str);
        
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            days[1] = 29;
        }
        day = Math.min(Integer.parseInt(date[2]), days[month - 1]);
        
        hour = Integer.parseInt(date[3]);
        minute = Integer.parseInt(date[4]);
        second = Integer.parseInt(date[5]);
        timePeriodFound = true;
    }
    
    public SignInDate(String[] date) {
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);

        check(year, month, String.join("-", date));
        
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            days[1] = 29;
        }
        day = Math.min(Integer.parseInt(date[2]), days[month - 1]);
        
        if (date.length > 3) {
            hour = Integer.parseInt(date[3]);
            minute = Integer.parseInt(date[4]);
            second = Integer.parseInt(date[5]);
            timePeriodFound = true;
        } else {
            hour = 0;
            minute = 0;
            second = 0;
        }
    }
    
    public SignInDate(int year, int month, int day) {
        check(year, month, String.format("%d-%d-%d", year, month, day));
        
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            days[1] = 29;
        }
        if (day > days[month - 1]) {
            day = days[month -1];
        }
        
        this.year = year;
        this.month = month;
        this.day = day;
        hour = 0;
        minute = 0;
        second = 0;
    }
    
    public SignInDate(int year, int month, int day, int hour, int minute, int second) {
        check(year, month, String.format("%d-%d-%d-%d-%d-%d", year, month, day, hour, minute, second));
        
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            days[1] = 29;
        }
        if (day > days[month - 1]) {
            day = days[month -1];
        }
        
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        timePeriodFound = true;
    }
    
    public SignInDate(String str) {
        String[] date = str.split("-");
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);

        check(year, month, str);

        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            days[1] = 29;
        }
        day = Math.min(Integer.parseInt(date[2]), days[month - 1]);
        
        if (date.length > 3) {
            hour = Integer.parseInt(date[3]);
            minute = Integer.parseInt(date[4]);
            second = Integer.parseInt(date[5]);
            timePeriodFound = true;
        } else {
            hour = 0;
            minute = 0;
            second = 0;
        }
    }

    private static void check(int year, int month, String str) {
        if (year < 1970 || year > Calendar.getInstance().get(Calendar.YEAR)) {
            throw new IllegalArgumentException("invalid year " + year + " from " + str);
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("invalid month " + month + " from " + str);
        }
    }
    
    public int getWeek() {
        int[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1];
    }
    
    public String getYearAsString() {
        return String.valueOf(year);
    }
    
    public String getMonthAsString() {
        if (month < 10) {
            return "0" + month;
        }
        return String.valueOf(month);
    }
    
    public String getDayAsString() {
        if (day < 10) {
            return "0" + day;
        }
        return String.valueOf(day);
    }
    
    public String getHourAsString() {
        if (hour < 10) {
            return "0" + hour;
        }
        return String.valueOf(hour);
    }
    
    public String getMinuteAsString() {
        if (minute < 10) {
            return "0" + minute;
        }
        return String.valueOf(minute);
    }
    
    public String getSecondAsString() {
        if (second < 10) {
            return "0" + second;
        }
        return String.valueOf(second);
    }
    
    public int compareTo(SignInDate date){
        long thisTime = getMillisecond();
        long anotherTime = date.getMillisecond();
        return Long.compare(thisTime, anotherTime);
    }
    
    public long getMillisecond() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute, second);
        return cal.getTimeInMillis();
    }
    
    public boolean hasTimePeriod() {
        return timePeriodFound;
    }
    
    public String getDataText(boolean timePeriod) {
        return timePeriod ? year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second : year + "-" + month + "-" + day;
    }
    
    public String getName(String format) {
        if (hour == -1 || minute == -1 || second == -1) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, day);
            return new SimpleDateFormat(format).format(new Date(cal.getTimeInMillis()));
        }
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute, second);
        return new SimpleDateFormat(format).format(new Date(cal.getTimeInMillis()));
    }
    
    @Override
    public String toString() {
        return getDataText(timePeriodFound);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SignInDate) {
            SignInDate date = (SignInDate) obj;
            return date.getYear() == year && date.getMonth() == month && date.getDay() == day;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.year;
        hash = 89 * hash + this.month;
        hash = 89 * hash + this.day;
        hash = 89 * hash + this.hour;
        hash = 89 * hash + this.minute;
        hash = 89 * hash + this.second;
        hash = 89 * hash + (this.timePeriodFound ? 1 : 0);
        return hash;
    }
    
    public static SignInDate getInstanceAsTimePeriod(String timePeriod) {
        String[] split = timePeriod.split(":");

        SignInDate today = SignInDate.getInstance(new Date());
        int hour = Integer.parseInt(split[0]);
        if (hour > 23 || hour < 0) {
            return null;
        }
        int minute = 0;
        if (split.length >= 2) {
            minute = Integer.parseInt(split[1]);
        }
        if (minute > 59 || minute < 0) {
            return null;
        }
        int second = 0;
        if (split.length >= 3) {
            second = Integer.parseInt(split[2]);
        }
        if (second > 59 || second < 0) {
            return null;
        }
        return new SignInDate(today.getYear(), today.getMonth(), today.getDay(), hour, minute, second);
    }
    
    public static SignInDate getInstance(String datatext) {
        return new SignInDate(datatext);
    }
    
    public static SignInDate getInstance(int year, int month, int day, int hour, int minute, int second) {
        return new SignInDate(year, month, day, hour, minute, second);
    }
    
    public static SignInDate getInstance(int year, int month, int day) {
        return new SignInDate(year, month, day);
    }
    
    public static SignInDate getInstance(String[] date) {
        return new SignInDate(date);
    }
    
    public static SignInDate getInstance(Date date) {
        return new SignInDate(date);
    }
    
    public static List<SignInDate> sort(List<SignInDate> dates) {
        dates.sort(SignInDate::compareTo);
        return dates;
    }
    
    public static int getContinuous(List<SignInDate> records) {
        int continuous = 0;
        if (records.isEmpty()) {
            return continuous;
        }
        int year = records.get(0).getYear();
        int month = records.get(0).getMonth();
        int day = records.get(0).getDay();
        for (SignInDate date : records) {
            date = SignInDate.getInstance(date.getYear(), date.getMonth(), date.getDay());
            boolean breakSign = true;
            if (year == date.getYear()) {
                int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                    days[1] = 29;
                }
                //Continuous sign-in is considered when the following conditions are met:
                if (days[month - 1] == day && month + 1 == date.getMonth() && date.getDay() == 1) {
                    continuous++;
                    breakSign = false;
                } else if (day + 1 == date.getDay()) {
                    continuous++;
                    breakSign = false;
                }
            } else if (year + 1 == date.getYear()) {
                if (month == 12 && date.getMonth() == 1 && day == 31 && date.getDay() == 1) {
                    continuous++;
                    breakSign = false;
                }
            }
            if (breakSign) {
                continuous = 1;
            }
            year = date.getYear();
            month = date.getMonth();
            day = date.getDay();
        }
        SignInDate today = getInstance(new Date());
        return today.getYear() != year || today.getMonth() != month || today.getDay() != day ? 0 : continuous;
    }
    
    public static int getContinuousOfMonth(List<SignInDate> dates) {
        int continuous = 0;
        if (dates.isEmpty()) {
            return continuous;
        }
        int year = dates.get(0).getYear();
        int month = dates.get(0).getMonth();
        int day = dates.get(0).getDay();
        for (SignInDate date : dates) {
            date = SignInDate.getInstance(date.getYear(), date.getMonth(), date.getDay());
            boolean breakSign = true;
            if (year == date.getYear() && month == date.getMonth()) {
                if (day + 1 == date.getDay()) {
                    continuous++;
                    breakSign = false;
                }
            }
            if (breakSign) {
                continuous = 1;
            }
            year = date.getYear();
            month = date.getMonth();
            day = date.getDay();
        }
        SignInDate today = getInstance(new Date());
        return today.getYear() != year || today.getMonth() != month || today.getDay() != day ? 0 : continuous;
    }
}
