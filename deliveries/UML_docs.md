# Documentazione UML
- Separazione tra Giocatore (Player) e Personaggio (Character)
- Abbiamo pensato il turno come una transazione ACID-like, composta da una serie di azioni (Action) che sono eseguite solo se tutte le condizion di tutte le azioni sono verificate. Ogni azione è quindi composta da una o più condizioni (Condition) e da uno o più effetti (Effect).
- Abbiamo sviluppato la struttura delle classi in modo che ogni classe "foglia" abbia una sola funzionalità, che sono gestite da classi composte che implementano le funzionalità complesse.
- La struttura è progettata per essere estensibile: per esempio se volessimo aggiungere una nuova arma basterebbe creare una nuova Entity e una nuova Action che descriva il funzionamento dell'arma (se possibile riutilizzando gli Effect e le Condition utilizzate in altre armi).
- Abbiamo generalizzato armi, powerup e munizioni in un classe Entity, perché hanno tutte una condizione da verificare ed un effetto.
- Ogni classe che contiene dati che hanno bisogno di essere spediti ha una sua rappresentazione leggera che può essere serializzata (es Game e GameData)
