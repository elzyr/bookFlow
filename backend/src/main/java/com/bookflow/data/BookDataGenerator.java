package com.bookflow.data;

import com.bookflow.author.Author;
import com.bookflow.author.AuthorRepository;
import com.bookflow.book.Book;
import com.bookflow.book.BookRepository;
import com.bookflow.category.Category;
import com.bookflow.category.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@Component
@Builder
public class BookDataGenerator implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        add_books();
    }

    void add_books() {
        List<Author> authors = new ArrayList<>(authorRepository.findAll());
        if(authors.isEmpty()) {
             authors.add( Author.builder()
                    .name("Andrzej Sapkowski")
                    .information("Światowy rozgłos przyniósł mu cykl wiedźmiński, opowiadający o losach Geralta z Rivii i innych wiedźminów. Jego utwory doczekały się licznych przekładów na języki obce oraz wielu ekranizacji. Część z nich została także przeniesiona do świata gier komputerowych.")
                    .build()
             );

             authors.add( Author.builder()
                    .name("Suzanne Collins")
                    .information("Suzanne Collins to amerykańska pisarka i autorka 'Igrzysk śmierci'  – bestsellerowej serii science–fiction dla młodzieży, której akcja rozgrywa się w futurystycznym państwie Panem. Cykl ten składa się na chwilę obecną z 4 tomów – każdy z nich niemal z miejsca trafiał na listy bestsellerów. Książki Collins zostały przetłumaczone na ponad 50 języków, w samych Stanach Zjednoczonych sprzedając się w nakładzie ponad 100 milionów egzemplarzy.  Trzy z nich doczekały się adaptacji filmowych, w których w rolę głównych bohaterów wcielili się Jennifer Lawrence oraz Josh Hutcherson. ")
                    .build()
             );

            authors.add( Author.builder()
                    .name("Cullen Bunn")
                    .information("Autor komiksów Bloodborne.")
                    .build()
            );

            authors.add( Author.builder()
                    .name("Valderrama Miguel")
                    .information("Autor komiksu Cyberpunk.")
                    .build()
            );

            authors.add( Author.builder()
                    .name("Ryszard Pagacz")
                    .information("Autor opracowań do matury z matematyki.")
                    .build()
            );

            authors.add( Author.builder()
                    .name("Bolesław Prus")
                    .information("Bolesław Prus, właśc. Aleksander Głowacki (ur. 20 sierpnia 1847 w Hrubieszowie, zm. 19 maja 1912 w Warszawie) – polski pisarz, prozaik, publicysta okresu pozytywizmu, współtwórca polskiego realizmu, kronikarz Warszawy, myśliciel i popularyzator wiedzy, działacz społeczny.")
                    .build()
            );

            authors.add( Author.builder()
                    .name("George Orwell")
                    .information("Orwell był niezwykłym krytykiem codzienności, inteligentnym i zarazem dowcipnym, sprawnie posługujacym się ironią w niemal każdym swoim tekście. Bardzo bliskie były mu tematy społeczne – w szczególności te dotyczące braku równości – a jako zwolennik socjalizmu demokratycznego zagorzale krytykował systemy totalitarne")
                    .build()
            );

            authorRepository.saveAll(authors);
        }

        List<Category> categories = new ArrayList<>(categoryRepository.findAll(Sort.by("categoryId")));
        if(categories.isEmpty()) {
            categories.add(
                Category.builder()
                .books(null)
                .category_name("fantastyka")
                .build()
            );

            categories.add(
                Category.builder()
                .books(null)
                .category_name("dla młodzieży")
                .build()
            );

            categories.add(
                  Category.builder()
                 .books(null)
                 .category_name("fantastyka dla młodzieży")
                 .build()
            );

            categories.add(
                Category.builder()
                .books(null)
                .category_name("komiksy")
                .build()
            );

            categories.add(
                Category.builder()
                .books(null)
                .category_name("podręczniki")
                .build()
            );

            categories.add(
                Category.builder()
                .books(null)
                .category_name("lektury")
                .build()
            );
            categoryRepository.saveAll(categories);
            categories = new ArrayList<>(categoryRepository.findAll(Sort.by("categoryId")));
        }



        List<Book> books = new ArrayList<>(bookRepository.findAll());
        if(books.isEmpty()){
            books.add( Book.builder()
                    .title("Wiedźmin. Tom 3. Krew elfów")
                    .yearRelease(2014)
                    .language("Polski")
                    .pageCount(340)
                    .description("Zagłębiając się w karty tego dzieła, zaczniesz odkrywać magiczny, wykreowany przez autora w sposób bardzo realistyczny, świat wiedźminów. Poznasz również samego Geralta, który postanawia zaopiekować się dzieckiem‑niespodzianką, którym jest dziewczyna o imieniu Ciri. Ważną postacią jest również czarodziejka Triss, która będzie miała duży wpływ na wychowanie dziecka.")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/3/8/3899905444438.jpg?store=default&image-type=large")
                    .authors(List.of(authors.getFirst()))
                    .categories(List.of(categories.getFirst()))
                    .build()
            );

            books.add( Book.builder()
                    .title("Igrzyska śmierci. Tom 1")
                    .yearRelease(2022)
                    .language("Polski")
                    .pageCount(352)
                    .description("Na ruinach dawnej Ameryki Północnej rozciąga się państwo Panem, z imponującym Kapitolem otoczonym przez dwanaście dystryktów. Okrutne władze stolicy zmuszają podległe sobie rejony do składania upiornej daniny. Raz w roku każdy dystrykt musi dostarczyć chłopca i dziewczynę między dwunastym a osiemnastym rokiem życia, by wzięli udział w Głodowych Igrzyskach, turnieju na śmierć i życie, transmitowanym na żywo przez telewizję. Bohaterką, a jednocześnie narratorką książki jest szesnastoletnia Katniss Everdeen, która mieszka z matką i młodszą siostrą w jednym z najbiedniejszych dystryktów nowego państwa. Katniss po śmierci ojca jest głową rodziny - musi troszczyć się o młodszą siostrę i chorą matkę, a jest to prawdziwa walka o przetrwanie...")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/9/9/9999906881999.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(1)))
                    .categories(List.of(categories.get(1), categories.get(2)))
                    .build()
            );

            books.add( Book.builder()
                    .title("Igrzyska śmierci. W pierścieniu ognia. Tom 2")
                    .yearRelease(2023)
                    .language("Polski")
                    .pageCount(360)
                    .description("W drugim tomie trylogii, W pierścieniu ognia Katniss i Peeta przygotowują się do odbycia obowiązkowego Tournee Zwycięzców, kiedy dowiadują się o fali zamieszek, do których przyczynił się ich zuchwały czyn. W tle trwają przygotowania do rocznicowych, 75. Głodowych Igrzysk, które przyniosą bardziej niż zaskakujący obrót spraw... Bo Kapitol jest zły. I Kapitol pragnie zemsty...")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/5/1/5199907017851.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(1)))
                    .categories(List.of(categories.get(1), categories.get(2)))
                    .build()
            );

            books.add( Book.builder()
                    .title("Igrzyska śmierci. Kosogłos. Tom 3")
                    .yearRelease(2023)
                    .language("Polski")
                    .pageCount(350)
                    .description("Trzeci tom serii Igrzyska śmierci. Katniss Everdeen już dwukrotnie stanęła na arenie Głodowych Igrzysk. Teraz mieszka w Trzynastce – legendarnym podziemnym dystrykcie, który wbrew kłamliwej propagandzie Kapitolu przetrwał, a co więcej, szykuje się do rozprawy z dyktatorską władzą. Katniss mimo początkowej niechęci, wykończona psychicznie i fizycznie ciężkimi przeżyciami na arenie, zgadza się zostać Kosogłosem – symbolem oporu przeciw kapitolińskiemu tyranowi.")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/1/2/1299907033912.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(1)))
                    .categories(List.of(categories.get(1), categories.get(2)))
                    .build()
            );

            books.add( Book.builder()
                    .title("Rok 1984")
                    .yearRelease(2022)
                    .language("Polski")
                    .pageCount(336)
                    .description("Okrutna i sugestywna wizja świata, w którym rządzi przemoc i strach, a władza panuje nie tylko nad losem człowieka, ale też nad jego myślami i uczuciami. Boleśnie aktualna opowieść o pragnieniu władzy i konsekwencjach jej nadużywania.")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/8/7/8799906775987.jpg?store=default&image-type=large")
                    .authors(List.of(authors.getLast()))
                    .categories(List.of(categories.getLast()))
                    .build()
            );

            books.add( Book.builder()
                    .title("Świat króla Artura")
                    .yearRelease(2021)
                    .language("Polski")
                    .pageCount(184)
                    .description("Oto świat Andrzeja Sapkowskiego. Świat twórcy sagi o wiedźminie, czarodzieja i wizjonera, który zawładnął naszą wyobraźnią. Ale na początku – zanim na scenę wkroczył wiedźmin Geralt – był mit. Opowieść o królu Arturze, rycerzach Okrągłego Stołu i Pani Jeziora. Romantyczna, krwawa, uwodzicielska i liryczna zarazem. Opowieść o wielkiej miłości, zdradzie i poszukiwaniu ideału, któremu na imię Graal. Do arturiańskich legend odwoływali się najwięksi: Szekspir, Milion, Blade, Petrarka, Dante, Goethe, Schiller, Kossak-Szczucka, Eco. A także wszyscy liczący się autorzy fantasy.")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/9/3/9399906749793.jpg?store=default&image-type=large")
                    .authors(List.of(authors.getFirst()))
                    .categories(List.of(categories.getFirst()))
                    .build()
            );

            books.add( Book.builder()
                    .title("Bloodborne. Królestwo posępnych cieni. Tom 4")
                    .yearRelease(2025)
                    .language("Polski")
                    .pageCount(112)
                    .description("Czwarty tom krwawej opowieści grozy ze świata wielokrotnie nagradzanej gry. Yharnam zostało dotknięte straszliwą plagą: na ulicach grasują bestie, a nowe zagrożenia czają się na każdym kroku. Mimo to łowcy potworów Gretchen i Abraham wyruszają na poszukiwania Luciena, swojego zaginionego podopiecznego. Para nieustraszonych wojowników będzie musiała zapuścić się w głąb podziemi i stanąć oko w oko z pradawnymi monstrami. Jaka straszliwa niespodzianka czeka ich na końcu drogi?...")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/8/8/8899907440688-3.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(2)))
                    .categories(List.of(categories.getFirst(),categories.get(3)))
                    .build()
            );

            books.add( Book.builder()
                    .title("Folwark zwierzęcy")
                    .yearRelease(2023)
                    .language("Polski")
                    .pageCount(272)
                    .description("Odrzucana przez znanych brytyjskich wydawców, również z przyczyn politycznych (wcześniej jej maszynopis omal nie został zniszczony w Londynie w eksplozji niemieckiej latającej bomby V-1), alegoryczna bajka Folwark zwierzęcy ukazała się w Anglii 17 sierpnia 1945 r. w nakładzie 4500 egzemplarzy. Od tamtej pory wydrukowano ich na świecie dziesiątki milionów, a brytyjski Book Marketing Council zaliczył Folwark zwierzęcy do dwunastu najznakomitszych dzieł czasów współczesnych. Pochodząca zeń fraza „równi i równiejsi” weszła do powszechnego obiegu. Genialne dzieło Orwella można przy tym odczytywać na wielu płaszczyznach: pozornie jest to satyra na rewolucję październikową i fundamentalny konflikt Stalina z Trockim, zarazem można je rozumieć jako mroczną, metaforyczną opowieść o ludzkiej niedoskonałości i niezmiennych prawach historii. Światek tytułowego folwarku to nie tylko świat za „żelazną kurtyną”, który w tej części Europy poznaliśmy na własnej skórze, lecz i symbolicznie ujęty epizod z dziejów ludzkości...")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/3/8/3899906936638.jpg?store=default&image-type=large")
                    .authors(List.of(authors.getLast()))
                    .categories(List.of(categories.getLast()))
                    .build()
            );

            books.add( Book.builder()
                    .title("Cyberpunk 2077. Trauma Team. Tom 1")
                    .yearRelease(2021)
                    .language("Polski")
                    .pageCount(96)
                    .description("Nadia, ratowniczka medyczna pracująca w Trauma Team International, jako jedyna przeżyła misję ratunkową, która zamieniła się w krwawą jatkę. Gdy decyduje się na powrót do pracy i udział w kolejnym zleceniu, odkrywa, że tym razem jego celem jest ocalenie mężczyzny odpowiedzialnego za śmierć jej partnerów z poprzedniego zespołu. Klient jej korporacji utknął na setnym piętrze wieżowca pełnego członków wrogiego gangu. Czy ta misja ma szansę na powodzenie? Autorem scenariusza jest Cullen Bunn (\"Deadpool\", \"Moon Knight\"), a rysunki przygotował Miguel Valderrama (\"Giants\"). Album zawiera zeszyty miniserii \"Cyberpunk 2077: Trauma Team\" #1–4.")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/x/7/x799906741107.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(2),authors.get(3)))
                    .categories(List.of(categories.getFirst(),categories.get(3)))
                    .build()
            );

            books.add( Book.builder()
                    .title("MATEMATYKA Zbiór zadań maturalnych Lata 2010–2024 Poziom podstawowy 1130 zadań")
                    .yearRelease(2024)
                    .language("Polski")
                    .pageCount(328)
                    .description("Zbiór zawiera zadania z lat 2010–2024, które występowały w arkuszach maturalnych CKE na poziomie podstawowym, a także zadania z arkuszy maturalnych w terminach dodatkowych (lipiec 2020 i czerwiec 2021-2024).")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/6/7/6799907342267.jpg?store=default&image-type=large")
                    .authors(List.of(authors.getLast()))
                    .categories(List.of(categories.get(4)))
                    .build()
            );

            books.add( Book.builder()
                    .title("MATEMATYKA Zbiór zadań maturalnych Lata 2002–2024 Poziom rozszerzony 575 zadań")
                    .yearRelease(2024)
                    .language("Polski")
                    .pageCount(342)
                    .description("Zbiór zawiera zadania z arkuszy maturalnych (na poziomie rozszerzonym) Centralnej Komisji Egzaminacyjnej z lat 2002–2024. Są tu zadania pochodzące z egzaminów maturalnych w terminach głównych i dodatkowych, z egzaminów próbnych przygotowanych przez CKE, z diagnozy przedmaturalnej z lat 2020–2022, z informatora o egzaminie maturalnym z matematyki z roku 2021, z arkusza pokazowego z roku 2022 oraz z arkuszy z sesji 2023 i 2024. ")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/x/8/x899907345108.jpg?store=default&image-type=large")
                    .authors(List.of(authors.getLast()))
                    .categories(List.of(categories.get(4)))
                    .build()
            );

            books.add( Book.builder()
                    .title("Gregor i klątwa Stałocieplnych. Seria Kroniki Podziemia. Tom 3")
                    .yearRelease(2016)
                    .language("Polski")
                    .pageCount(362)
                    .description("Po wypełnieniu dwóch przepowiedni przychodzi kolej na Przepowiednię Krwi, która wymaga, by Gregor i Botka wrócili do Podziemia i uratowali jego mieszkańców od zarazy. Jednak tym razem mama nie chce ich puścić… chyba że będzie mogła im towarzyszyć.")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/7/1/7199906220271-7553.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(1)))
                    .categories(List.of(categories.get(1),categories.get(2)))
                    .build()
            );

            books.add( Book.builder()
                    .title("Lalka")
                    .yearRelease(2025)
                    .language("Polski")
                    .pageCount(846)
                    .description("Lalka Bolesława Prusa to utwór wyjątkowy, od momentu ukazania się na łamach „Kuriera Codziennego” budzący kontrowersje, ale też podziw. Zawikłana, pełna niedopowiedzeń historia źle ulokowanych uczuć, straconych złudzeń i zaprzepaszczonych możliwości. Jedyna w swoim rodzaju konfrontacja romantycznego i pozytywistycznego idealizmu z realizmem.")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/8/5/8599907439485.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(5)))
                    .categories(List.of(categories.getLast()))
                    .build()
            );

            books.add( Book.builder()
                    .title("Katarynka")
                    .yearRelease(2004)
                    .language("Polski")
                    .pageCount(32)
                    .description("\"Katarynka\" jest także tytułem noweli Bolesława Prusa, opowiadającej o losach niewidomej dziewczynki i jej sąsiada. ")
                    .jpg("https://cdn.swiatksiazki.pl/media/catalog/product/7/7/7799900010077.jpg?store=default&image-type=large")
                    .authors(List.of(authors.get(5)))
                    .categories(List.of(categories.getLast()))
                    .build()
            );

            categories.getFirst().setBooks(new ArrayList<>(List.of(
                    books.getFirst(),
                    books.get(5),
                    books.get(6),
                    books.get(8)
            )));

            categories.get(1).setBooks(new ArrayList<>(List.of(
                    books.get(1),
                    books.get(2),
                    books.get(3),
                    books.get(11)
            )));

            categories.get(2).setBooks(new ArrayList<>(List.of(
                    books.get(1),
                    books.get(2),
                    books.get(3),
                    books.get(11)
            )));

            categories.get(3).setBooks(new ArrayList<>(List.of(
                    books.get(6),
                    books.get(8)
            )));

            categories.get(4).setBooks(new ArrayList<>(List.of(
                    books.get(9),
                    books.get(10)
            )));

            categories.get(5).setBooks(new ArrayList<>(List.of(
                    books.stream().filter(b -> b.getTitle().contains("Lalka")).findFirst().orElseThrow(),
                    books.stream().filter(b -> b.getTitle().contains("Katarynka")).findFirst().orElseThrow(),
                    books.stream().filter(b -> b.getTitle().contains("Folwark zwierzęcy")).findFirst().orElseThrow(),
                    books.stream().filter(b -> b.getTitle().contains("Rok 1984")).findFirst().orElseThrow()
            )));


            bookRepository.saveAll(books);
            categoryRepository.saveAll(categories);
        }
    }



}
