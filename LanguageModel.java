import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
            String window = "";
            char c;
            In in = new In(fileName);
            // Reads just enough characters to form the first window
            for (int i = 0; i < windowLength; i++) {
                if(in.isEmpty()){
                    break;
                }
            }
            // Processes the entire text, one character at a time
            while (!in.isEmpty()) {
            // Gets the next character
            c = in.readChar();
            // Checks if the window is already in the map
            List probs = CharDataMap.get(window);
            // If the window was not found in the map
            // Creates a new empty list, and adds (window,list) to the map
            if(probs == null){
                probs = new List();
                CharDataMap.put(window, probs);
            }
            // Calculates the counts of the current character.
            probs.update(c);
            // Advances the window: adds c to the window’s end, and deletes the
            // window's first character.
            window = window.substring(1) + c;           
            }
            // The entire file has been processed, and all the characters have been counted.
            // Proceeds to compute and set the p and cp fields of all the CharData objects
            // in each linked list in the map.
            for (List probs : CharDataMap.values()){
                calculateProbabilities(probs);
            }
        }
	

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
        int countChr = 0; 
        //sum all characters
        for (CharData cd : probs.toArray()){
            countChr += cd.count;
        }
        //compute cp and p
        double cumProbilitis = 0.0;
        for(CharData cd : probs.toArray()){
            double probaliti = (double) cd.count / countChr;
            cumProbilitis += probaliti;
            cd.p = probaliti;
            cd.cp = cumProbilitis;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		// Your code goes here
        double r = randomGenerator.nextDouble();
        for (CharData cd : probs.toArray()) {
            if (r < cd.cp){
                return cd.chr;
            }  
        }
        return ' '; // if we doesn't find a suit character to return for this r
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Your code goes here
        //check if initialText length long to use seed for genrate 
        if(windowLength > initialText.length()){
            return initialText;
        }
        String geneText = initialText;
        //generate text chrcter , if lastSub not found break
        for (int i = 0; i < textLength; i++) {
            String lastSub = geneText.substring(geneText.length() - textLength);
            if(CharDataMap.containsKey(lastSub)){
                char nextChr = getRandomChar(CharDataMap.get(lastSub));
                geneText += nextChr; 
            }else{
                break;
            } 
        } 
        return geneText;
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
