package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private boolean isTeleport = false;
    private int teleportTick = 0;
    private int countTeleport = 1;
    private GameObject target;
    //private GameObject teleportGweh;
    // private GameObject teleportGw;
    
    // private int countTeleport = 1;
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
            if(bot.getSize()<99999){
                playerAction.heading = getHeadingBetween(foodList.get(0));
                playerAction.action = PlayerActions.FORWARD; 
            }
            if(enemies.get(0).size > bot.getSize() && getDistanceBetween(bot, enemies.get(0)) < 100){
                int i=0;
                while(getDistanceBetween(bot, foodList.get(i))>getDistanceBetween(bot, enemies.get(0))){
                    i++;
                }
                playerAction.heading = getHeadingBetween(foodList.get(i));
                playerAction.action = PlayerActions.FORWARD;
                // System.out.println("Tidak ada ancaman terdekat, mencari makanan..\n");
            }
            

            if(getDistanceBetween(obstacleList.get(0), bot) < 100){
                int i=0;
                while(getDistanceBetween(foodList.get(i), obstacleList.get(0))<obstacleList.get(0).getSize()){
                    i++;
                }
                playerAction.heading = getHeadingBetween(foodList.get(i));
                playerAction.action = PlayerActions.FORWARD; 
                // System.out.println("Tidak ada ancaman terdekat, mencari makanan..\n");
            }

            // ========== bot bucin (nembak) ===============
            playerAction.heading = getHeadingBetween(foodList.get(0));
            // kondisi kalo size player >= 50 dia bakal nembakin torpedo ke lawan jenis terdekat
            if (bot.size >= 30 && (getDistanceBetween(bot, enemies.get(0))<125 + bot.getSize() + enemies.get(0).getSize())) {
                playerAction.heading = getHeadingBetween(enemies.get(0));
                playerAction.action = PlayerActions.FIRETORPEDOES;
                // System.out.println("Musuh terdeteksi, menembak player terdekat..\n");
            }
            // ========== end of bot bucin (nembak) ===============
            
            if (projectileList.size() > 0 && getDistanceBetween(projectileList.get(0), bot) < 100 && bot.getSize()>40){
                playerAction.heading = getHeadingBetween(projectileList.get(0));
                playerAction.action = PlayerActions.ACTIVATESHIELD;
                // System.out.println("Bot terancam, mengaktifkan shield..\n");
            }
            var distanceFromCenter = getDistanceBetween(bot, gameState.world);
            if(distanceFromCenter + (1.5 * bot.size) > gameState.world.radius){
                playerAction.heading = getHeadingBetween(gameState.world);
                // System.out.println("Bot mendekati danger zone, kembali ke safe zone..\n");
            }


            if (gameState.world.getCurrentTick() % 100 == 0) {
                this.countTeleport++;
            }

            //int teleportTick = 0;

            // if (idTeleport == null) {
            //     System.out.println("Null");
            // } else {
            //     System.out.println("False");
            // }
            //getTeleport();
            var availableTargets = gameState
                .getPlayerGameObjects()
                .stream().filter(enemy -> enemy.id != bot.id && enemy.size < bot.size) // && getDistanceBetween(enemy, bot) < 200 + bot.size + enemy.size)
                .sorted(Comparator.comparing(enemy -> getDistanceBetween(this.bot, enemy), Comparator.reverseOrder()))
                .collect(Collectors.toList());
            System.out.println("target"); System.out.println(target);

            
                
            // kondisi waktu nembak teleport
            // if (!availableTargets.isEmpty()){
            //     if (this.bot.size > availableTargets.get(0).size && this.bot.size > 30 && !isTeleport && this.countTeleport > 0) {
            //         this.target = availableTargets.get(0);
            //         playerAction.heading = getHeadingBetween(this.target);
            //         playerAction.action = PlayerActions.FIRETELEPORT;
            //         //teleportTick++;
            //         this.countTeleport--;
            //         this.isTeleport = true;
            //         System.out.println("Mangsa terdeteksi, menembakkan teleport..\n");
            //         // get target musuh buat teleport
            //     }
                
            // }

            // if (isTeleport && !teleportList.isEmpty()) {
            //     if (!teleportList.isEmpty()) {
            //         UUID idTeleport = gameState
            //             .getGameObjects()
            //             .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
            //             .sorted(Comparator.comparing(item -> getDistanceBetween(this.bot, item)))
            //             .collect(Collectors.toList())
            //             .get(0).id;
            //         teleportFound = true;

            //         GameObject teleportGweh = gameState
            //         .getGameObjects()
            //         .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER && item.id == idTeleport)
            //         .sorted(Comparator.comparing(item -> getDistanceBetween(this.bot, item)))
            //         .collect(Collectors.toList())
            //         .get(0);

            //         if (getDistanceBetween(teleportGweh, enemies.get(0)) < 50) {
            //             playerAction.heading = getHeadingBetween(enemies.get(0));
            //             playerAction.action = PlayerActions.TELEPORT;
            //             // System.out.println("Teleport berhasil dilakukan");
            //         }
            //     }
            // }   


            // if (isTeleport && teleportTick + gameState.world.currentTick % 100 == 0){
            //     playerAction.heading = getHeadingBetween(enemies.get(0));
            //     playerAction.action = PlayerActions.TELEPORT;
            //     System.out.println("Teleport mendekati mangsa, mengaktifkan teleport\n");
            // }

            if (!availableTargets.isEmpty() && this.bot.size > 80 && !isTeleport && countTeleport > 0) {
                // teleportTick++;
                this.target = availableTargets.get(0);
                this.isTeleport = true;
                playerAction.heading = getHeadingBetween(this.target);
                playerAction.action = PlayerActions.FIRETELEPORT;
                countTeleport--;
                this.teleportTick = gameState.world.getCurrentTick() + (int) (getDistanceBetween(target, bot) / 20);
                System.out.println("Berhasil menembakkan teleport");
            }

            if (this.isTeleport && gameState.world.getCurrentTick() == this.teleportTick) {
                playerAction.action = PlayerActions.TELEPORT;
                playerAction.heading = getHeadingBetween(this.target);
                this.isTeleport = false;
                this.target = null;
                this.teleportTick = 0;
                System.out.println("Berhasil terleport");
            }
            
            // System.out.println("tele"); System.out.println(this.teleportGw);

             // saat class bot udah punya telepornya dan kondisi waktu dia trigger teleportnyah
            // if (this.teleportGw != null && this.isTeleport) {

            //     if (getDistanceBetween(this.teleportGw, this.target) < this.bot.size + this.target.size) {
            //         playerAction.heading = getHeadingBetween(this.target);
            //         playerAction.action = PlayerActions.TELEPORT;
            //         System.out.println("Berhasil teleport");
            //         this.isTeleport = false;
            //         this.teleportGw = null;
            //         this.target = null;
            //     }

                // GameObject teleportGweh = gameState
                //     .getGameObjects()
                //     .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER && item.id == idTeleport)
                //     .sorted(Comparator.comparing(item -> getDistanceBetween(this.bot, item)))
                //     .collect(Collectors.toList())
                //     .get(0);
    
            //}
    

            // // delete teleport waktu keluar safe zone
            // if (this.teleportGw != null && getTeleportList().isEmpty()) {
            //     this.isTeleport = false;
            //     this.teleportGw = null;
            //     this.target = null;
            //     System.out.println("Berhasil menghapus teleport");
            // }

        }
        this.playerAction = playerAction;
    }

    // public void getTeleport() {
    //     var teleportList = gameState
    //     .getGameObjects()
    //     .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
    //     .sorted(Comparator.comparing(item -> getDistanceBetween(this.bot, item)))
    //     .collect(Collectors.toList());
    //     if (this.isTeleport && !teleportList.isEmpty() && this.teleportGw == null) {
    //         this.teleportGw = teleportList.get(0);
    //         System.out.println("Berhasil get teleport");
    //     }
    // }


    // public List<GameObject> getTeleportList() {
    //     var teleportList = gameState
    //     .getGameObjects()
    //     .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
    //     .sorted(Comparator.comparing(item -> getDistanceBetween(this.bot, item)))
    //     .collect(Collectors.toList());
    //     return teleportList;
    // }

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
