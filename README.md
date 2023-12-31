# PiAF, un quizz pour apprendre les chants d'oiseaux
English description of the code below.
## A propos de ce jeu.
Vous pouvez lire ce [petit descriptif](/app/src/main/assets/about.html).
## Prochaines étapes
Voilà quelques fonctionnalités qui vont être ajoutées dans les prochaines versions:
* associer un contenu audio descriptif d'une vingtaine de secondes à certains oiseau.
* créer un catalogue des sons consultables classés par oiseau.
* associer à chaque oiseaux des milieux de prédilection:
    * prairie
    * sous-bois
    * rivière
    * parc urbain
    * forêt
    * lac, étang
    * vasière
    * plages
    * falaises
    * bâti
    * jardins
    * friches, buissons
    * pelouses
## Installer le jeu
Vous pourrez prochainement trouver le jeu sur le Google Store. Patience...
## Prochaines étapes
## Pour les développeurs
This game was developped in Java for Android. I used an sqlite database with ORMlite. The sound resources are not available on GitHub because they are protected by a specific licence.
### Data model
Here are the class used in this project.
#### Bird
A bird is defined by the following attributes:
* id: a unique identifier auto generated by the ORM.
* french: it is the french name of the bird.
* latin: it is the latin name of the bird.
* imagePath: the name of the graphical resource for the bird, which is stored [here](/app/src/main/res/drawable).
* audioDescriptionPath: the name of the audio resource used to give information about the bird. This feature is not developped yet.
* url: an url pointing to the webpage of the bird on the site [Oiseaux.net](https://www.oiseaux.net/).
* sounds: the collection of sounds for this bird.
#### Sound
A sound is defined by the following attributes:
* id: a unique identifier auto generated by the ORM.
* bird: self explanatory, the DB field contains the bird id.
* type: the type of sound (cri, chant...)
* path: the name of the audio resource for the bird, which is stored [here](/app/src/main/res/raw). ***Please note that the sounds are not currently stored on my github because of property right restrictions.***
* credit: the creator of the sound.
* level: difficulty level of the sound
* scores: the collection of scores for this sound.
#### Score
A score is a record of the player's answer for a given question.
* dateMillis: it is the timestamp in milliseconds for the question's answer and it is a unique id as well.
* sound: the sound played in the question
* score: 0 if wrong, 1 if correct
* answeredBird: the proposition made by the player, if in QCM mode.
* SCORES_DEPTH: a public attribute which corresponds to the number of questions used to validate the level. Current value is 40.
* VALIDATION_PERCENTAGE: a public attribute which corresponds to the rate of correct answers needed to validate the level. Current value is 90.
#### Level
Here are the attributes:
* id: a unique identifier.
* french: the name of the level
* imagePath: the resource used to display the level in the welcoming screen.
* levelValidationImagePath: the resource used to celebrate the level validation in the welcoming screen. It is an animated gif.
* sounds: the collection of sounds for this level.
#### User
This class is used to store the user preferences.
* id: not relevant since there is only one user.
* qcm: true if QCM mode, false if free mode.
* level: current level reached by the player.
* nbQuestions: number of questions for each quizz.
* nbChoices: number of proposed choices in QCM mode.
### Utilities
#### DataBaseHelper
This class is an extension of OrmLiteSqliteOpenHelper and it contains the methods used to handle the database's transactions, such as the oncreate and udpate methods and the getters for the DAO, i.e. the objects used to execute DB queries such as objects' persistance or update. Last, it contains additionnal methods related to level validation. 
***NB: these methods might be included in the QuizzHelper instead.***
#### QuizzHelper
This class contains specific methods dedicated to the quizz generation: pick up a random sound, generate other choices for the QCM. It contains the logic of the game.
### Views and controllers
I followed Android recommended practices. Therefore, I used a main fragment container included into an main view. Then, all the different views of my app corresponds to updates of this main fragment managed by the FragmentManager from the main activity.
#### Welcome Fragment
The first fragment found by the user: it shows the current level, and the minimum number of right answers to reach the next level.
As well, when the users passes the level, it displays a gif animation.
#### Free Fragment
The fragment used to play the game in free mode.
#### QCM Fragment
The fragment used to play the game in QCM mode.
#### Answers Fragment
The fragment used to show the score and the answers when completing the game.
### Resources
#### html pages
The *about*, *credits* and *licence* pages are written in html and stored in the assets [assets folder](/app/src/main/assets).
#### sounds
They are stored in the [raw folder](/app/src/main/res/raw).
#### other assets
I followed Android usage to store other assets such as graphical assets, strings, menus... 