package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private boolean isTeleport = true;
    // private GameObject worldCenter;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        playerAction.action = PlayerActions.FORWARD; 
        playerAction.heading = new Random().nextInt(360);

        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD ||
                                             item.getGameObjectType() == ObjectTypes.SUPERFOOD ||
                                             item.getGameObjectType() == ObjectTypes.SUPERNOVA_PICKUP)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var enemies = gameState
                    //.getGameObjects()
                    .getPlayerGameObjects()
                    .stream().filter(enemy -> enemy.id != this.bot.id)
                    //.filter(enemy -> enemy.id != bot.id)
                    .sorted(Comparator.comparing(enemy -> getDistanceBetween(this.bot, enemy)))
                    .collect(Collectors.toList());
            
            var projectileList = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO || 
                                             item.getGameObjectType() == ObjectTypes.TELEPORTER ||
                                             item.getGameObjectType() == ObjectTypes.SUPERNOVA_BOMB)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var obstacleList = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD ||
                                             item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
                
            var wormhole = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.WORMHOLE)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            //Handle case if there is no food left
            if(foodList.isEmpty()){
                playerAction.action = PlayerActions.FORWARD; 
                playerAction.heading = getHeadingBetween(gameState.world)+90 % 360;
            } else {
            //Basic greedy for food
                playerAction.heading = getHeadingBetween(foodList.get(0));
                playerAction.action = PlayerActions.FORWARD; 
            }
            //Maneuverability if nearest food has an enemy near it
            if(enemies.get(0).size > bot.getSize() && getDistanceBetween(bot, enemies.get(0)) < 100){
                playerAction.heading = 180+getHeadingBetween(foodList.get(0)) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
            //Maneuverability if nearest food within an obstacle
            if(getDistanceBetween(obstacleList.get(0), bot) < 100){
                int i=0;
                while(getDistanceBetween(foodList.get(i), obstacleList.get(0))<obstacleList.get(0).getSize()){
                    i++;
                }
                playerAction.heading = getHeadingBetween(foodList.get(i));
                playerAction.action = PlayerActions.FORWARD; 
            }

            // ========== bot bucin (nembak) ===============
            playerAction.heading = getHeadingBetween(foodList.get(0));
            // kondisi kalo size player >= 50 dia bakal nembakin torpedo ke lawan jenis terdekat
            if (bot.size >= 50 && (getDistanceBetween(bot, enemies.get(0))<400 + bot.getSize() + enemies.get(0).getSize())) {
                playerAction.heading = getHeadingBetween(enemies.get(0));
                playerAction.action = PlayerActions.FIRETORPEDOES;
            }
            // ========== end of bot bucin (nembak) ===============
            //Defensive mechanism on any incoming projectile
            if (projectileList.size() > 0 && getDistanceBetween(projectileList.get(0), bot) < 100 && bot.getSize()>40){
                playerAction.action = PlayerActions.ACTIVATESHIELD;
            }
            //Redirection if heading to edge of map
            var distanceFromCenter = getDistanceBetween(bot, gameState.world);
            if(distanceFromCenter + (1.5 * bot.size) > gameState.world.radius){
                playerAction.heading = getHeadingBetween(gameState.world);
                playerAction.action = PlayerActions.FORWARD;
            }
            
            int teleportTick = 0;
            if (bot.getSize() > enemies.get(0).size && bot.getSize() > 30 && getDistanceBetween(bot, enemies.get(0)) > 100){
                playerAction.heading = getHeadingBetween(enemies.get(0));
                playerAction.action = PlayerActions.FIRETELEPORT;
                teleportTick++;
                isTeleport = true;
            }
            if (isTeleport && teleportTick + gameState.world.currentTick % 200 == 0){
                playerAction.action = PlayerActions.TELEPORT;
            }
        }
        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }
    private double getDistanceBetween(GameObject object1, World object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.centerPoint.x);
        var triangleY = Math.abs(object1.getPosition().y - object2.centerPoint.y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }
    private int getHeadingBetween(World otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.centerPoint.y - bot.getPosition().y,
                otherObject.centerPoint.x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }


}
        