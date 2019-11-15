package cn.com.infosec.netcert.kmcAdmin.ui.login;

import java.util.Arrays;
import java.util.List;

public class panelDemo {
    private String name;
    private int age;
    private String[] arr;
    private List list;

    /**
     * µØ·½
     */
    public panelDemo() {
    }

    public panelDemo(String name, int age, String[] arr, List list) {
        this.name = name;
        this.age = age;
        this.arr = arr;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String[] getArr() {
        return arr;
    }

    public void setArr(String[] arr) {
        this.arr = arr;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "panelDemo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", arr=" + Arrays.toString(arr) +
                ", list=" + list +
                '}';
    }
}
