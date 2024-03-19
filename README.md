<a name="readme-top"></a>


[![LinkedIn][linkedin-shield]][linkedin-url]


<div>
<h1 align="center">Music Library App</h1>
  <p align="center">
    <strong>Java, JDBC & PostgreSQL</strong>
    <br />
    <a href="https://github.com/Peepstar/music-library-app"><strong>Explore the docs »</strong></a>
    <br />
    <br /> 
    <a href="https://github.com/Peepstar/music-library-app/issues">Report Bug</a>
    ·
    <a href="https://github.com/Peepstar/music-library-app/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Music Library app to manage a file with over 100.000+ records for songs, artist, genres and albums.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Java][Java.java]][Java-url]
* [![PostgreSQL][PostgreSQL.sql]][PostgreSQL-url]
* [![Hibernate][Hibernate.java]][Hibernate-url]



<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

This guide will help you set up and run the Music library app locally on your machine.

### Prerequisites

Ensure you have the following installed:

- Java Development Kit (JDK)
- Git
- IntelliJ IDEA or your preferred Java IDE

### Steps to Setup

**1. Clone the repo**

   ```bash
   git clone https://github.com/Peepstar/music-library-app.git
   ```

**2. Create PostgreSQL Database**

- Open your PostgreSQL client (e.g., pgAdmin or psql).
- Create Database:
 ```bash
   CREATE DATABASE music_library;
   ```
- Connect to Database:
```bash
   \c music_library;
   ```

**3. Configure PostgreSQL connection as per your installation**

- Open `src/main/java/musiclibrary/ConnectToDB`
- Update `private static final String user` and `private static final String password` to your PostgreSQL configuration.

**4. Configure path to the CSV file as per your local path**

- Open `src/main/java/musiclibrary/PopulateData`
- Update `String csvFile = "path//to//your//project"`

**5. Run the app by running the Menu file**

  ```bash
  Menu.java
  ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- USAGE EXAMPLES -->

## Usage

#### Just choose any option in the menu to perform CRUD operations. Any option can perform operations in any item(tracks, genres, artists, specific fields)

##### 1. ADD/POST
##### 2. DELETE
##### 3. UPDATE/PUT
##### 4. READ/GET
##### 5. DROP Database
##### 0. EXIT application




<p align="right">(<a href="#readme-top">back to top</a>)</p>



See the [open issues](https://github.com/Peepstar/music-library-app/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Julian David Peña Rojas - julianpr8@hotmail.com

Project Link: [https://github.com/Peepstar/music-library-app](https://github.com/Peepstar/music-library-app)

<p align="right">(<a href="#readme-top">back to top</a>)</p>






<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/julian-peña-java
[Java.java]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/es/
[PostgreSQL.sql]: https://img.shields.io/badge/postgresql-4169e1?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/
[Hibernate.java]: https://img.shields.io/badge/JDBC-BA?style=for-the-badge
[Hibernate-url]: https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html

