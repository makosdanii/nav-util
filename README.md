# nav-util
A frontend **Vue.js + Vuetify** keretrendszer használatával készül, ami egy felől kommunikál a szintén általam fejlesztett **Java Spring** alapú backend szerverrel,
más részt a **Mapbox** weboldal API felületéről kér le adatokat.

A szerveren tárolásra kerülnek felhasználói adatok, a szükséges **regisztráció** után.
Spring Security gondoskodik arról, hogy csak a bejelentkezett felhasználó menthessen **útvonal terveket**, létrehozhasson megtekinteni kívánt **helyszínek listáját**, elérje a sajátjait.
Generált adatok MySQL adatbázisban lesznek tárolva.

A felhasználói térképek a **mapbox-gl package** segítségével lesznek vizualizálva.
Azon kívül, hogy mozgatható, forgatható, billenthető térkép-alapot ad, ez képes lesz kirajolzni két pont közötti útvonalat,  vagy bármely koordinátát megjeleníteni.
Különböző helyszínlistához tartozó helyszínekhez kirajzolhatja a koordinátákat más-más **marker**-rel.
