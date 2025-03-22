# Projet_Labyrinthe

## 📋 Aperçu du projet

Ce projet est une application JavaFX développée dans le cadre d’un TP universitaire. Elle permet de résoudre des labyrinthes en utilisant les algorithmes **DFS (Depth-First Search)** et **BFS (Breadth-First Search)**. L’application propose une interface graphique moderne et intuitive, avec des fonctionnalités avancées telles que l’animation du chemin, des icônes sur les boutons, et la possibilité de sauvegarder les solutions.

---

## ✨ Fonctionnalités principales

### Gestion des labyrinthes
- **Chargement** : Chargez un labyrinthe depuis un fichier texte (ex. `labyrinth.txt`).
- **Génération aléatoire** : Créez un labyrinthe aléatoire avec un chemin garanti entre le départ (`S`) et la sortie (`E`).
- **Personnalisation de la taille** : Choisissez les dimensions du labyrinthe généré (largeur et hauteur).

### Résolution
- **Algorithmes** : Résolvez le labyrinthe avec DFS ou BFS.
- **Comparaison des performances** : Affiche le nombre d’étapes et le temps d’exécution dans l’interface graphique et la console.
- **Gestion des cas sans solution** : Affiche un message clair si aucun chemin n’est trouvé.

### Interface graphique
- **Visualisation** :
  - Murs en gris, départ en vert, sortie en rouge, chemin en bleu.
  - Animation progressive du chemin (200 ms par étape).
- **Boutons intuitifs** : Chaque bouton est accompagné d’une icône pour une meilleure expérience utilisateur.
- **Thème visuel** : Interface moderne avec des couleurs harmonieuses et des effets de survol sur les boutons.

### Sauvegarde et sortie
- **Sortie textuelle** : Affiche le labyrinthe résolu dans la console avec des `+` pour le chemin.

---

## 🛠️ Prérequis

Pour exécuter ce projet, vous devez avoir les éléments suivants installés :

- **Java** : Version 17 ou supérieure (disponible sur [AdoptOpenJDK](https://adoptopenjdk.net/) ou [Oracle](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)).
- **JavaFX** : SDK JavaFX 17 ou supérieur (téléchargeable sur [Gluon](https://gluonhq.com/products/javafx/)).
- **IDE** : IntelliJ IDEA (recommandé) ou un autre IDE compatible avec JavaFX (comme Eclipse ou NetBeans).
- **Maven** : Optionnel, si tu utilises Maven pour gérer les dépendances (assure-toi que `pom.xml` est configuré correctement).
- **Git** : Pour cloner le dépôt (disponible sur [Git](https://git-scm.com/downloads)).

---

## 📦 Installation et configuration

Suivez ces étapes pour configurer et exécuter le projet :

### 1. Cloner le dépôt
Clonez ce dépôt sur votre machine locale :
```bash
git clone https://github.com/votre-utilisateur/Projet_Labyrinthe_Gx.git
```
### 2. Ouvrez le projet dans IntelliJ IDEA (ou un autre IDE).
### 3. Configurez JavaFX :
Ajoutez le SDK JavaFX à votre projet (File > Project Structure > Libraries > Ajouter JavaFX).
Configurez les arguments VM dans la configuration de lancement :
```bash
--module-path /chemin/vers/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
```
### 4. Assurez-vous que le dossier ```bash src/main/resources/icons/``` contient les icônes nécessaires ```bash(file.png, labyrinth.png, DFS.png, BFS.png, circular.png)```.
- Si les icônes sont pas dans le dossier, l’application fonctionnera, mais les boutons n’auront pas d’icônes.
Utilisation

### 5. Lancez l’application en exécutant la classe Main.java.
L’interface graphique s’ouvre avec un labyrinthe par défaut.
Utilisez les boutons pour interagir :
  - Load Maze : Chargez un labyrinthe depuis un fichier .txt (exemple fourni : labyrinth.txt).
  - Generate Maze : Générez un labyrinthe aléatoire (7x7 par défaut).
  - Solve with DFS : Résolvez avec l’algorithme DFS.
  - Solve with BFS : Résolvez avec l’algorithme BFS.
  - Reset : Réinitialisez l’affichage du labyrinthe.
### 6. Observez les résultats :
Le chemin s’affiche progressivement en bleu (animation).
Les performances (étapes et temps) sont affichées dans l’interface et la console.
Si aucun chemin n’est trouvé, un message s’affiche en rouge.
### 7. Exemple de fichier labyrinth.txt
Voici un exemple de fichier que vous pouvez charger :

```bash
########
S#==#= #
# # ## #
# ###= #
# #E#= #
########
```
Placez ce fichier dans le dossier racine de votre projet.

### 8. Structure du projet
 src/main/java/com/example/labyrinthsolver/ :
 - Main.java : Point d’entrée de l’application.
 - Labyrinth.java : Logique du labyrinthe (chargement, génération, résolution).
 - LabyrinthView.java : Interface graphique.
 - src/main/resources/icons/ : Contient les icônes pour les boutons.

### 👥 Auteurs
- Maman Nafy Ndiaye
- Oumar Yoro Diouf
- Mamadou Biaye
