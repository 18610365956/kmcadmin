package cn.com.infosec.netcert.kmcAdmin.ui.login;

public enum Color {
    Red("��ɫ", 1), Green("��ɫ", 2), Black("��ɫ", 3);
    private String name;
    private int index;

    Color(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static void toSTring() {
        String color = "��ɫ";
        int index = showIndex(color);
        System.out.println(index);
    }


    private static int showIndex(String color) {
        Color[] values = Color.values();
        for (Color c :
                values) {
            if ("��ɫ".equalsIgnoreCase(c.getName())) {
                return c.getIndex();
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        toSTring();
    }

}
