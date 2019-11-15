package cn.com.infosec.netcert.kmcAdmin.ui.login;

public enum Color {
    Red("红色", 1), Green("绿色", 2), Black("黑色", 3);
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
        String color = "红色";
        int index = showIndex(color);
        System.out.println(index);
    }


    private static int showIndex(String color) {
        Color[] values = Color.values();
        for (Color c :
                values) {
            if ("红色".equalsIgnoreCase(c.getName())) {
                return c.getIndex();
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        toSTring();
    }

}
