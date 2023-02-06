package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private GameObject worldCenter;

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
        playerAction.heading = 270;

        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
                    
                foodList.addAll(gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList()));
                
                foodList.addAll(gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVA_PICKUP)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList()));

            var enemyList = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            
            var projectileList = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

                projectileList.addAll(gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList()));
                
                projectileList.addAll(gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVA_BOMB)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList()));

            var obstacleList = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

                obstacleList.addAll(gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList()));
                
            var wormhole = gameState.getGameObjects()    
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.WORMHOLE)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            // if(bot.getSize()<600){
            //     playerAction.heading = getHeadingBetween(foodList.get(0));
            // } else {//(bot.getSize()>=600){
            //     if(enemyList.get(0).size>bot.getSize()){
            //         playerAction.heading = 180-getHeadingBetween(enemyList.get(0));
            //     } else {
            //         playerAction.heading = getHeadingBetween(enemyList.get(0));
            //     }
            // }
            playerAction.heading = getHeadingBetween(foodList.get(0));
            
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

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }


}
