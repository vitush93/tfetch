/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zapoctak.misc;

/**
 *
 * @author vitush
 */
public class BodyEndStateMachine extends StateMachine {

    public BodyEndStateMachine() {
        state = 0;
        finalState = 7;
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
                if (c == '/') {
                    state = 6;
                } else {
                    state = 0;
                }
            case 6:
                if (c == '>') {
                    state = 7;
                } else {
                    state = 0;
                }
        }
    }

}
