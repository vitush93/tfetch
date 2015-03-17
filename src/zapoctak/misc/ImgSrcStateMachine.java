package zapoctak.misc;

public class ImgSrcStateMachine extends StateMachine {

    private StringBuilder sb;
    private int yieldState;

    public ImgSrcStateMachine() {
        sb = new StringBuilder();
        state = 0;
    }

    @Override
    public void next(int n) {
        char c = (char) n;
        
        switch(state) {
            
        }
    }

    public String yield() throws InvalidOperationException {
        if (state != yieldState) {
            throw new InvalidOperationException("Not ready to yield.");
        }

        String res = sb.toString();
        sb.setLength(0);

        return res;
    }

}
