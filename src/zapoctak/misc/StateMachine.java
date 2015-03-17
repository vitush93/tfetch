/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zapoctak.misc;

import java.util.HashMap;

/**
 *
 * @author vitush
 */
public abstract class StateMachine {

    protected int state;
    protected int finalState;

    public boolean match() {
        return state == finalState;
    }

    public abstract void next(int n);
}
