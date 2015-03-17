package zapoctak.misc;

public class BodyStartStateMachine extends StateMachine {

    public BodyStartStateMachine() {
        state = 0;
        finalState = 6;
    }

    @Override
    public void next(int n) {
        char c = (char) n;

        switch (state) {
            case 0:
                if (c == '<') {
                    state = 1;
                }
            case 1:
                if (c == 'b') {
                    state = 2;
                } else {
                    state = 0;
                }
            case 2:
                if (c == 'o') {
                    state = 3;
                } else {
                    state = 0;
                }
            case 3:
                if (c == 'd') {
                    state = 4;
                } else {
                    state = 0;
                }
            case 4:
                if (c == 'y') {
                    state = 5;
                } else {
                    state = 0;
                }
            case 5:
                if (c == '>') {
                    state = 6;
                }
        }
    }

}
