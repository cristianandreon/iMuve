========================================================================
    LIBRERIA A COLLEGAMENTO DINAMICO: cenni preliminari sul progetto jni2dll
========================================================================

La creazione guidata applicazione ha creato questa DLL jni2dll.  

Questo file contiene un riepilogo del contenuto di ciascun file che fa parte
dell'applicazione jni2dll.


jni2dll.vcproj
    File di progetto principale per i progetti VC++ generati tramite una creazione guidata applicazione. 
    Contiene informazioni sulla versione di Visual C++ che ha generato il file e 
    informazioni sulle piattaforme, le configurazioni e le caratteristiche del 
    progetto selezionate con la creazione guidata applicazione.

jni2dll.cpp
    File di origine della DLL principale.

	Una volta creata, questa DLL non esporta alcun simbolo. Di conseguenza, non   
	produrrà un file LIB quando viene generata. Se si desidera impostare questo progetto  
	come dipendenza di un altro progetto sarà necessario  
	aggiungere il codice per esportare alcuni simboli dalla DLL in modo da produrre una libreria di esportazione  
	oppure impostare la proprietà Ignora libreria di input su Sì 
	nella pagina delle proprietà Generale della cartella Linker nella finestra di dialogo  
	Pagine delle proprietà del progetto.

/////////////////////////////////////////////////////////////////////////////
Altri file standard:

StdAfx.h, StdAfx.cpp
    Tali file vengono utilizzati per generare il file di intestazione
    precompilato jni2dll.pch e il file dei tipi precompilato .obj.

/////////////////////////////////////////////////////////////////////////////
Altre note:

la creazione guidata applicazione utilizza i commenti "TODO:" per indicare le parti del
 codice sorgente da aggiungere o personalizzare.

/////////////////////////////////////////////////////////////////////////////
