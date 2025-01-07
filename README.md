Projet Poseiden

Description

Poseiden est une application financière conçue pour gérer des opérations CRUD sur des entités financières tout en garantissant une sécurité robuste et une expérience utilisateur fluide. Le projet s'appuie sur des technologies modernes comme Spring Boot, Spring Data JPA et Spring Security.

Fonctionnalités

Gestion des utilisateurs avancée :

Seul l'administrateur peut ajouter de nouveaux utilisateurs.

Possibilité de se connecter via GitHub.

Connexion sécurisée via un identifiant (username) et validation des mots de passe (au moins 8 caractères, un chiffre et un symbole).

Gestion des utilisateurs avancée :

Seul l'administrateur peut ajouter de nouveaux utilisateurs.

Possibilité de se connecter via GitHub.

Opérations CRUD : Gestion de six entités , notamment BidList, CurvePoint, Rating, RuleName, Trade et User.

Authentification et Sécurité :

Authentification basée sur les sessions.

Contrôle d'accès basé sur les rôles.

Gestion des utilisateurs :

Connexion via username.

Validation des mots de passe (au moins 8 caractères, un chiffre et un symbole).

Intégration Frontend :

Prend en charge l'intégration avec une interface utilisateur existante.

Validation des données :

Validation des entrées pour les champs numériques et mots de passe complexes.

Technologies utilisées

Backend :

Java 17

Spring Boot 3.x

Spring Data JPA

Spring Security

Base de données :

MySQL 8.0

Tests :

JUnit 5

Mockito

Outil de construction :

Maven

Instructions d'installation

Prérequis

Java 17

Maven 3.8+

MySQL 8.0

Étapes pour exécuter

Cloner le dépôt :

git clone https://github.com/votreutilisateur/poseiden.git
cd poseiden

Configurer la base de données :

Créez une base de données MySQL nommée demo.

Mettez à jour application.properties avec vos identifiants de base de données :

spring.datasource.url=jdbc:mysql://localhost:3306/poseiden
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update

Construire le projet :

mvn clean install

Lancer l'application :

mvn spring-boot:run

Accéder à l'application :

L'API backend sera disponible sur http://localhost:8080.

Initialisation de la base de données

Utilisez le script SQL fourni pour créer et initialiser la base de données.

Un utilisateur administrateur est créé par défaut avec les informations suivantes :

Nom d'utilisateur : admin

Mot de passe : password123.

Structure du projet

src/main/java :

Contient tout le code Java de l'application.

Principaux packages :

controller - Gestion des pages web et de la logique des vues pour l'application monolithique.

service - Logique métier pour chaque entité.

repository - Interfaces pour les opérations de base de données avec JPA.

model - Classes d'entités représentant les tables de la base de données.

dto - Objets de transfert de données utilisés pour la communication API.

sécurité : regroupe les composants responsables de la gestion de la sécurité de l'application, incluant l'authentification des utilisateurs, le contrôle d'accès basé sur les rôles, et les configurations de session sécurisée.

validation : inclut des annotations et des mécanismes spécifiques pour assurer la validation des données utilisateur et des champs d'entrée, garantissant ainsi leur conformité aux exigences métier.

src/test/java :

Tests unitaires et tests d'intégration pour l'application.

Mise en œuvre de la sécurité

Les mots de passe sont hachés avec bcrypt pour un stockage sécurisé.

Les endpoints sensibles sont protégés avec des annotations @PreAuthorize.

L'authentification est basée sur les sessions pour une expérience utilisateur fluide et sécurisée.

Tests

Exécuter les tests unitaires avec :

mvn test

Les rapports de couverture de code sont générés avec le plugin jacoco.
