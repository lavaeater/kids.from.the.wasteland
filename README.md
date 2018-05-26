# Beamon RPG / Kids from the Wasteland

## Boken om spelet

Jag har nu mergat in alla ändringar från Beamon Innovation day i spelet. Great!

Hur är allt det här uppbyggt och hur är det gjort och hur ändrar man något?

Jag tror ju mycket på emergent architecture, så jag har låtit en kaosapa sakta 
men säkert skapa det mest kaotiska någonsin...

Nej, skämt åsido. Jag har försökt hålla saker separerade, det finns till och med 
lite tester (framför allt på FactsOfTheWorld-delen av spelet).

## Ramverk, verktyg, tools, mayhem

Med i projektet ligger ett visualstudioprojekt för spritesheet management. Det
kan importera spritesheets för PixiJS och göra om dem till TexturePacker-formatet
som LibGDX använder. Provided as is and it is shit. Det ligger under tools.

Själva projektet ligger under kftw2.

För att modda, skriva och koda kan man dra ner Android Studio eller IntelliJ IDEA.

Vad har jag då använt? 

LibGDX (https://github.com/libgdx/libgdx)

LibKTX, kotlin "wrapper" för LibGDX (https://github.com/libktx/ktx)

gdx-setup (för att skapa projektet, bättre gradle-struktur än den officiella) 
(https://github.com/czyzby/gdx-setup)

För pixel art, appen pixaki för iPAD (https://rizer.co/pixaki/)

För tiles och sprites, open game art (http://opengameart.org), specifikt

https://opengameart.org/content/micro-tileset-overworld-and-dungeon

https://opengameart.org/users/calciumtrice

Online pixel-editor: https://piskelapp.com - kan exportera spritesheets med metadata
för PixiJS som jag sen kan konvertera till texturepacker... oh my god.

## Teknisk inspiration

So, so much. GDC talks är en guldgruva för inspiration för hur man implementerar
saker...

Men, OK:

### Kartgenerering med Perlin Noise

https://www.redblobgames.com/maps/terrain-from-noise/

### Ink för dynamisk dialog

https://www.inklestudios.com/ink/

GDC talk: https://www.youtube.com/watch?v=KYBf6Ko1I2k

### Global state för dialog? Nej, story. Nej, eeh, för regler och fakta?

Det här gjorde jag klart bara dagen innan Innovation Day och är kanske en onödigt 
komplicerad datastruktur för att spara information och nycklar så att man kan göra
state-kollar i spelet. Så, när spelaren träffar en Beamon-kollega så kollar vi om 
det finns någon regel som matchar det som just hände (typ "träffatkollega, kollega=Ulrica
TräffatsFörut=nej osv) och om den regeln kan ge oss en konversation att köra.
I vårt spel hade vi bara en regel - vi kör en dialog, men dialogen är sen dynamiskt
modifierad beroende på om vi träffat kollegan tidigare osv. The sky is the limit.

GDC Talk: https://www.gdcvault.com/play/1015317/AI-driven-Dynamic-Dialog-through
