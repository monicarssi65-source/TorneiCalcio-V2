# ⚽ TorneiCalcio v2

Applicazione Android nativa sviluppata in **Kotlin** per la gestione completa e dinamica di tornei calcistici (Campionati, Tornei a eliminazione diretta, Gruppi + Eliminazione).

## 🚀 Caratteristiche Principali
* **Room DB v2**: Architettura locale solida per il salvataggio di tornei, squadre, partite e marcatori, con migrazioni distruttive gestite.
* **Classifiche in Tempo Reale**: Calcolo automatico di punti, gol fatti/subiti, differenza reti e posizioni in classifica in base ai risultati inseriti.
* **Classifica Cannonieri**: Gestione dettagliata dei marcatori dei match con tracciamento dei minuti e dei gol per ogni giocatore.
* **Esportazione PDF**: Generazione di report professionali e puliti con tabelle di riepilogo di classifica e cannonieri tramite *iText7*.
* **Condivisione Nativa**: Integrazione con *FileProvider* per condividere istantaneamente i PDF generati tramite WhatsApp, Email o altre app di sistema.

## 🛠️ Stack Tecnico
* **Linguaggio**: Kotlin
* **Database**: Room Persistence Library (v2.6.1)
* **UI**: Material Components, ViewBinding, ConstraintLayout, RecyclerView
* **Architettura**: MVVM (ViewModel, LiveData, Coroutines)
* **Librerie Terze**: iText7 Core (7.2.5) per la generazione di documenti PDF

## 🤖 Build & Automazione
Il progetto è predisposto per l'integrazione con **GitHub Actions**. Ogni commit sul branch `main` attiva automaticamente un workflow di compilazione cloud che genera l'eseguibile (APK) pronto per l'installazione.
