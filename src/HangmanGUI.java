import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class HangmanGUI extends Application {

	private String currentWord; // the randomly selected word
	private TextField guessField; // the user enters their guess here
	private Text currentWordText; // show the current word (with - for unguessed letters)
	private Text outcomeText; // show the outcome of each guess and the game
	private Text wrongGuessesText; // show a list of incorrect guesses
	private Text wrongGuessNumberText; // show how many incorrect guesses (or how many guesses remain)
	private final static int MAX_WRONG_GUESSES = 7;
	private static final Color TITLE_AND_OUTCOME_COLOR = Color.rgb(221, 160, 221);
	private static final Color INFO_COLOR = Color.rgb(224, 255, 255);
	private static final Color WORD_COLOR = Color.rgb(224, 255, 255);
	
	private String displayMessage="",  userGuess="";
	private String [] displayCurrentWord;
	private String [] currentWordArray;
	private ArrayList<String> guessedLetterList ;
	private ArrayList<String> wrongGuessLetterList;
	private int wrongGuessCounter=0;
	private int revealedLetterCounter=0;
	

	public void start(Stage primaryStage) {

		VBox mainVBox = new VBox();
		mainVBox.setStyle("-fx-background-color: royalblue");
		mainVBox.setAlignment(Pos.CENTER);
		mainVBox.setSpacing(10);

		Text welcomeText = new Text("Welcome to Hangman!");
		welcomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 36));
		welcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		Text introText1 = new Text("Guess a letter.");
		Text introText2 = new Text("You can make " + MAX_WRONG_GUESSES + " wrong guesses!");
		introText1.setFont(Font.font("Helvetica", 24));
		introText1.setFill(INFO_COLOR);
		introText2.setFont(Font.font("Helvetica", 24));
		introText2.setFill(INFO_COLOR);

		VBox introBox = new VBox(welcomeText, introText1, introText2);
		introBox.setAlignment(Pos.CENTER);
		introBox.setSpacing(10);
		mainVBox.getChildren().add(introBox);

		// create before game is started
		outcomeText = new Text("");
		guessField = new TextField();
		wrongGuessNumberText = new Text("");
		currentWord = chooseWord();
		String start="";
		
		for(int i=0; i<currentWord.length();i++) {
			start+= "-";
		}
		currentWordText = new Text(start);
		//currentWordText = new Text(currentWord);
		//currentWordText = new Text(currentWordDisplay(userGuess));
		wrongGuessesText = new Text("Wrong Guesses: []");

		
		currentWordText.setFont(Font.font("Helvetica", FontWeight.BOLD, 48));
		currentWordText.setFill(WORD_COLOR);
		HBox currentBox = new HBox(currentWordText);
		currentBox.setAlignment(Pos.CENTER);
		currentBox.setSpacing(10);
		mainVBox.getChildren().add(currentBox);

		Text guessIntroText = new Text("Enter your guess: ");
		guessIntroText.setFont(Font.font("Helvetica", 26));
		guessIntroText.setFill(INFO_COLOR);
		guessField.setOnAction(this::handleGuessField);
		HBox guessBox = new HBox(guessIntroText, guessField);
		guessBox.setAlignment(Pos.CENTER);
		guessBox.setSpacing(10);
		mainVBox.getChildren().add(guessBox);

		outcomeText.setFont(Font.font("Helvetica", 28));
		outcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		HBox outcomeBox = new HBox(outcomeText);
		outcomeBox.setAlignment(Pos.CENTER);
		outcomeBox.setSpacing(10);
		mainVBox.getChildren().add(outcomeBox);

		wrongGuessesText.setFont(Font.font("Helvetica", 24));
		wrongGuessesText.setFill(INFO_COLOR);
		HBox wrongGuessesBox = new HBox(wrongGuessesText);
		wrongGuessesBox.setAlignment(Pos.CENTER);
		wrongGuessesBox.setSpacing(10);
		mainVBox.getChildren().add(wrongGuessesBox);

		wrongGuessNumberText.setFont(Font.font("Helvetica", 24));
		wrongGuessNumberText.setFill(INFO_COLOR);
		HBox wrongGuessNumberBox = new HBox(wrongGuessNumberText);
		wrongGuessNumberBox.setAlignment(Pos.CENTER);
		mainVBox.getChildren().add(wrongGuessNumberBox);

		Scene scene = new Scene(mainVBox, 550, 500);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private void handleGuessField(ActionEvent event) {
		// YOUR CODE HERE
		do {
			userGuess = guessField.getText();
			
			if(isGuessValid(userGuess)) {
			//if( it is not guessed){
				if(!isGuessed(userGuess)) {
					displayMessage = "You've Guessed. Try again";	
				}else {// is not guessed
					displayMessage ="Correct guess!";
					guessedLetterList.add(userGuess);
					
				}
			//update it 
			}else {//useGuess is not valid
				if(isInWrongGuessList(userGuess)) {
					displayMessage = "You've guessed this wrong letter again!";
				}else {
					wrongGuessLetterList.add(userGuess);
					wrongGuessCounter++;
					displayMessage = "Wrong guess! Try again! ";
				}
					
			}
			currentWordText.setText(displayMessage);
			guessField.clear();
			
		}while(keepPlaying(revealedLetterCounter,wrongGuessCounter));
	
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private String currentWordDisplay(String userGuess) {
		String s ="";
		for(int i=0; i<displayCurrentWord.length;i++) {
			if(userGuess.equalsIgnoreCase(displayCurrentWord[i])) {
				displayCurrentWord[i]= userGuess;
				revealedLetterCounter++;
			}
		for(int j=0; i<displayCurrentWord.length; j++) {
		
			s += displayCurrentWord[i];
		}	
		}
		return s;
		
		
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isGuessValid(String userGuess) {
	    currentWordArray = new String[currentWord.length()];
		
		for(int i=0; i<currentWordArray.length;i++) {

			currentWordArray[i]= String.valueOf((currentWord.charAt(i)));	
		}
		for(int i=0; i<currentWordArray.length; i++) {
			if(userGuess.equalsIgnoreCase(currentWordArray[i])) {
				return true;
			}
		}
		return false;		
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isGuessed(String userGuess) {
		guessedLetterList = new ArrayList<>();
		if(!guessedLetterList.isEmpty()) {
			for(int i=0; i<guessedLetterList.size();i++) {
				if(userGuess.equalsIgnoreCase(guessedLetterList.get(i))) {
					return true;	
				}
			}
		}
		return false;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isInWrongGuessList(String userGuess) {
		wrongGuessLetterList = new ArrayList<>();
		if(!wrongGuessLetterList.isEmpty()) {
			for(int i=0; i<wrongGuessLetterList.size();i++) {
				if(userGuess.equalsIgnoreCase(wrongGuessLetterList.get(i))) {
					return true;
				}
			}
		}
		return false;
		
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean keepPlaying(int revealedLetterCounter, int wrongGuessCounter) {
		String userAnswer= "";
		Scanner input = new Scanner(System.in);
		if(wrongGuessCounter<8) {
			if(revealedLetterCounter<currentWordArray.length){
				return true;
			}else {
				displayMessage = "You've won the game! Play again?";
				userAnswer = input.nextLine();
				if(userAnswer.equalsIgnoreCase("Y")){
					return true;
				}else {
					return false;
				}
			}
		}else {
			displayMessage = "You've lost The game! play again? ";
			userAnswer = input.nextLine();
			if(userAnswer.equalsIgnoreCase("Y")){
				return true;
			}else {
				return false;
			}
			
		}
	}
		
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private  String chooseWord() {
		// YOUR CODE HERE
		ArrayList<String> wordList = new ArrayList<String>();
		Scanner fileScan =null;
		try {
			fileScan = new Scanner(new FileReader(new File("words.txt")));
		}catch(FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
		
		while(fileScan.hasNext()) {
			String word = fileScan.nextLine();
			wordList.add(word);	
		}
		fileScan.close();
		//create a random to generate a random number to choose a word from the arrayList.
		Random rand = new Random();
		currentWord = wordList.get(rand.nextInt(wordList.size()));
		return currentWord;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		launch(args);
		
	}

}
