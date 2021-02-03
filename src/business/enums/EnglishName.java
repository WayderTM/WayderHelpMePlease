package business.enums;

public enum EnglishName {
    А("А", "a"), Б("Б", "b"), В("В", "v"), Г("Г", "g"), Д("Д", "d"), Е("Е", "e"), Ё("Ё", "e"), Ж("Ж", "zh"), З("З", "z"), И("И", "i"), Й("Й", "i"), К("К", "k"), Л("Л", "l"), М("М", "m"), Н("Н", "n"), О("О", "o"), П("П", "p"), Р("Р", "r"), С("С", "s"), Т("Т", "t"), У("У", "y"), Ф("Ф", "f"), Х("Х", "h"), Ц("Ц", "c"), Ч("Ч", "ch"), Ш("Ш", "sh"), Щ("Щ", "sh"), Ю("Ю", "u"), Я("Я", "ya");

    public String ruschar;
    public String engchar;
    EnglishName(String ruschar, String engchar) {
        this.ruschar = ruschar;
        this.engchar = engchar;
    }
}