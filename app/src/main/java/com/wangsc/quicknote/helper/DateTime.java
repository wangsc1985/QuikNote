package com.wangsc.quicknote.helper;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by 阿弥陀佛 on 2015/6/24.
 */
public class DateTime extends GregorianCalendar {

    public DateTime() {
    }

    public static DateTime fromCalendar(Calendar calendar) {
        return new DateTime(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH),
                calendar.get(HOUR_OF_DAY), calendar.get(MINUTE), calendar.get(SECOND));
    }

    public DateTime(int year, int month, int day) {
        this.set(year, month, day, 0, 0, 0);
        this.set(Calendar.MILLISECOND, 0);
    }

    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        this.set(year, month, day, hour, minute, second);
        this.set(Calendar.MILLISECOND, 0);
    }

    public static DateTime getToday() {
        DateTime today = new DateTime();
        return today.getDate();
    }

    /**
     * 返回一个时、分、秒、毫秒置零的此DateTime副本。
     *
     * @return
     */
    public DateTime getDate() {
        return new DateTime(this.get(YEAR), this.get(MONTH), this.get(DAY_OF_MONTH));
    }

    public DateTime addMonths(int months) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(MONTH, months);
        return dateTime;
    }

    public DateTime addDays(int days) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(DAY_OF_MONTH, days);
        return dateTime;
    }

    public DateTime addHours(int hours) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(HOUR_OF_DAY, hours);
        return dateTime;
    }

    public int getYear() {
        return this.get(YEAR);
    }

    public int getMonth() {
        return this.get(MONTH)+1;
    }

    public int getDay() {
        return this.get(DAY_OF_MONTH);
    }

    public String toShortDateString() {
        return StringHelper.concat(this.getYear() + "年", this.getMonth()  + "月", this.getDay() + "日");
    }

    public String toLongDateString() {
        return StringHelper.concat(this.getYear() + "/",
                this.getMonth() < 10 ? "0" + this.getMonth() + "/" : this.getMonth()  + "/",
                this.getDay() < 10 ? "0" + this.getDay() + " " : this.getDay() + " ",
                this.get(HOUR_OF_DAY) < 10 ? "0" + this.get(HOUR_OF_DAY) + ":" : this.get(HOUR_OF_DAY) + ":",
                this.get(MINUTE) < 10 ? "0" + this.get(MINUTE) + ":" : this.get(MINUTE) + ":",
                this.get(SECOND) < 10 ? "0" + this.get(SECOND) : this.get(SECOND) + "");
    }
}
