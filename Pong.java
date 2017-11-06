package p1;

// OpenGL (Open Graphics Library)
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;

// GLFW (OpenGL Framework)
import static org.lwjgl.glfw.GLFW.*;

public class Pong {

    // Handle (or 'ID') for our window
    private long window;
    
    // Window dimensions
    private final int SCREEN_WIDTH = 640;
    private final int SCREEN_HEIGHT = 480;
    
    // Variables tracking size, speed, and location of objects in the game
    private int player_paddle_width = 15;
    private int player_paddle_height = 60;
    
    private int player_paddle_x = 60;
    private int player_paddle_y = 240;
    
    private int computer_paddle_width = 15;
    private int computer_paddle_height = 60;
    
    private int computer_paddle_x = 580;
    private int computer_paddle_y = 240;
    
    private int ball_width = 15;
    private int ball_height = 15;
    
    private int ball_x = 300;
    private int ball_y = 240;
    
    private int ball_vx = -3;
    private int ball_vy = -2;
    
    /**
     *  Draw a rectangle with top left corner at (x, y)
     *  
     * @param x     X coordinate of the rectangle's top-left corner.
     * @param y     Y coordinate of the rectangle's top-left corner.
     * @param w     width of the rectangle.
     * @param h     height of the rectangle.
     */
    public void drawRect(int x, int y, int w, int h) {
        glBegin(GL_QUADS);
            glVertex2f(x    , y    );
            glVertex2f(x + w, y    );
            glVertex2f(x + w, y + h);
            glVertex2f(x    , y + h);
        glEnd();
    }
    
    /**
     * Draws the player's paddle.
     */
    public void drawPlayer() {
        glColor3f(1.f, 1.f, 1.f);
        drawRect(player_paddle_x, player_paddle_y,
                player_paddle_width, player_paddle_height);
    }
    
    /**
     * Draw the computer's paddle.
     */
    public void drawComputer() {
        glColor3f(1.f, 1.f, 1.f);
        drawRect(computer_paddle_x, computer_paddle_y,
                computer_paddle_width, computer_paddle_height);
    }
    
    /**
     * Draw the ball.
     */
    public void drawBall() {
        glColor3f(1.f, 1.f, 1.f);
        drawRect(ball_x, ball_y, ball_width, ball_height);
    }
    
    /**
     * Draw the 'net'.
     */
    public void drawNet() {
        glColor3f(1.f, 1.f, 1.f);
        glBegin(GL_LINES);
            glVertex2f( 320, 0   );
            glVertex2f( 320, 480 );
        glEnd();
    }
    
    /**
     * Very simplified collision detection.
     * 
     * @return  An integer representing the type of collision detected
     *          (This should really use an enumerated type...)
     */
    public int hitCheck() {
        
        // If the ball and player paddle are intersecting...
        if (colliding(ball_x, ball_y, ball_width, ball_height,
                player_paddle_x, player_paddle_y,
                player_paddle_width, player_paddle_height)) return 1;
        
        // If the ball and computer paddle are intersecting...
        if (colliding(ball_x, ball_y, ball_width, ball_height,
                computer_paddle_x, computer_paddle_y,
                computer_paddle_width, computer_paddle_height)) return 2;
        
        // If the ball is outside of vertical bounds...
        if (ball_y < 0 || ball_y > SCREEN_HEIGHT - ball_height ) return 3;
        
        // If the ball has gotten past the player...
        if (ball_x < 0) return -1;
        
        // If the ball has gotten past the computer...
        if (ball_x > SCREEN_WIDTH - ball_width) return -2;
        
        return 0;
    }
    
    /**
     * Checks if two rectangles are intersecting.
     * 
     * @param x0    X coordinate of the first rectangle's top-left corner.
     * @param y0    Y coordinate of the first rectangle's top-left corner.
     * @param w0    Width of the first rectangle.
     * @param h0    Height of the first rectangle.
     * 
     * @param x1    X coordinate of the first rectangle's top-left corner.
     * @param y1    Y coordinate of the first rectangle's top-left corner.
     * @param w1    Width of the first rectangle.
     * @param h1    Height of the first rectangle.
     * 
     * @return      True if the rectangles intersect, false otherwise.
     */
    public boolean colliding(int x0, int y0, int w0, int h0,
                             int x1, int y1, int w1, int h1) {
        
        if(x0 + w0 < x1) return false;
        if(y0 + h0 < y1) return false;
        
        if(x1 + w1 < x0) return false;
        if(y1 + h1 < y0) return false;
        
        return true;
    }
    
    /**
     * Run the game!
     */
    public void run() {
        init();
        loop();
    }
    
    /**
     * Initialize libraries, set up some initial variables and settings.
     */
    public void init() {
        
        glfwInit();
        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        
        window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Pong", 0, 0);
        
        if (window == 0)
            System.err.println("Failed to create window.");
        
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
            
            if (key == GLFW_KEY_UP && action == GLFW_PRESS )
                player_paddle_y -= 25;
            
            if (key == GLFW_KEY_DOWN && action == GLFW_PRESS )
                player_paddle_y += 25;
        });
        
        glfwMakeContextCurrent(window);
        
        glfwSwapInterval(1);
        
        glfwShowWindow(window);
        
        GL.createCapabilities();
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 1);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
    
    /**
     * This is our main game loop. It will continuously draw and
     * update our game objects. 
     */
    public void loop() {
        glClearColor(0.f, 0.f, 0.f, 1.f);
        
        while(!glfwWindowShouldClose(window)) {
            
            glClear(GL_COLOR_BUFFER_BIT);
            
            drawPlayer();
            drawComputer();
            drawNet();
            drawBall();
            
            switch(hitCheck()) {
            case 1:
                ball_vx--;
                ball_vx *= -1;
                
                ball_vy = (player_paddle_y - ball_y) / 10;
                break;
                
            case 2:
                ball_vx++;
                ball_vx *= -1;
                
                ball_vy = (computer_paddle_y - ball_y) / 10;
                break;
                
            case 3:
                ball_vy *= -1;
                break;
                
            case -1:
                ball_x = player_paddle_x + player_paddle_width;
                ball_y = (player_paddle_y + player_paddle_height / 2) - (ball_height / 2);
                
                ball_vx = 2;
                ball_vy = 0;
                break;
                
            case -2:
                ball_x = computer_paddle_x;
                ball_y = (computer_paddle_y + computer_paddle_height / 2) - (ball_height / 2);
                
                ball_vx = -2;
                ball_vy = 0;
                break;
            }
            
            ball_x += ball_vx;
            ball_y += ball_vy;
            
            if (computer_paddle_y + computer_paddle_height / 2.f < ball_y)
                computer_paddle_y += 2;
            else if (computer_paddle_y + computer_paddle_height / 2.f > ball_y)
                computer_paddle_y -= 2;
            
            glfwSwapBuffers(window);
            
            glfwPollEvents();
        }
    }
    
    /**
     * Main method, entry point for the JVM.
     * 
     * @param args  command line arguments.
     */
    public static void main(String[] args) {
        
        // Create an instance of our game and run it!
        new Pong().run();
    }
}