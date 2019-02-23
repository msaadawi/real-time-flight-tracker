<h1>Realtime Flights Tracker</h1>
<h3>Description:</h3>
This project is a desktop application to manage, track and visualize an airline company flights in realtime, created using JavaFX, Java, HTML, JavaScript, MySQL and Google Maps API.
<h3>Features:</h3>
- Manage airplanes.<br/>
- See which airplanes are actives and which are not, in realtime.<br/>
- Manage flights.<br/>
- See terminated, current and future flights in realtime.<br/>
- Visualize flights in Google Maps in realtime.
<h3>Usage:</h3>
<b>note:</b> Google Maps only works when using Java 11+<br/><br/>
1. Download the project or clone it.<br/>
2. Open Root folder in your favorite Java IDE.<br/>
3. Add needed libraries (can be found in 'lib' folder) to your classpath.<br/>
4. Setup a database user and grant him access to the database named 'rft'.<br/>
5. modify the file 'DButil.java' by changing the database connection credentials with the ones you gave to the user created in the previous step.<br/>
6. Execute the 'Realtime-Flights-Tracker.sql' sql file.<br/>
7. Download openjfx<br/>
8.Add these configurations to your VM options :<br/>
--module-path
path_to_openjfx/javafx-sdk-11.0.2/lib
--add-modules=javafx.controls,javafx.fxml,com.jfoenix
--add-exports
javafx.controls/com.sun.javafx.scene.control.behavior=com.jfoenix
--add-exports
javafx.base/com.sun.javafx.binding=com.jfoenix
--add-exports
javafx.graphics/com.sun.javafx.stage=com.jfoenix
--add-exports
javafx.base/com.sun.javafx.event=com.jfoenix
--add-exports
javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.util=ALL-UNNAMED
--add-exports
javafx.base/com.sun.javafx.logging=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.prism=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.glass.utils=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.font=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
--add-exports
javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.input=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.prism.paint=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.scenario.effect=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.scenario.effect.impl.prism=ALL-UNNAMED<br/>
9. Open the 'application' package and compile/run the 'Main.java' class.
