package com.flansmod.common.driveables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.flansmod.utils.ConfigMap;
import com.flansmod.utils.ConfigUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

import com.flansmod.client.model.ModelDriveable;
import com.flansmod.common.FlansMod;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.EnumFireMode;
import com.flansmod.common.paintjob.PaintableType;
import com.flansmod.common.parts.PartType;
import com.flansmod.common.types.TypeFile;
import com.flansmod.common.vector.Vector3f;
import com.flansmod.common.driveables.collisions.CollisionShapeBox;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DriveableType extends PaintableType {
    /** The plane model */
    public ModelDriveable model;

    //Health and recipe
    /**
     * Health of each driveable part
     */
    public HashMap<EnumDriveablePart, CollisionBox> health = new HashMap<>();
    public HashMap<EnumDriveablePart, BoxExplosion> partDeathExplosions = new HashMap<>();
    /**
     * Recipe parts associated to each driveable part
     */
    public HashMap<EnumDriveablePart, ItemStack[]> partwiseRecipe = new HashMap<>();
    /**
     * Recipe parts as one complete list
     */
    public ArrayList<ItemStack> driveableRecipe = new ArrayList<>();

    //Ammo
    /**
     * If true, then all ammo is accepted. Default is true to minimise backwards compatibility issues
     */
    public boolean acceptAllAmmo = true;
    /**
     * The list of bullet types that can be used in this driveable for the main gun (tank shells, plane bombs etc)
     */
    public List<BulletType> ammo = new ArrayList<>();

    //Harvesting variables
    /**
     * If true, then this vehicle harvests blocks from the harvester hitbox and places them in the inventory
     */
    public boolean harvestBlocks = false;
    /**
     * What materials this harvester eats
     */
    public ArrayList<Material> materialsHarvested = new ArrayList<>();
    public boolean collectHarvest = false;
    public boolean dropHarvest = false;
    public Vector3f harvestBoxSize = new Vector3f(0, 0, 0);
    public Vector3f harvestBoxPos = new Vector3f(0, 0, 0);
    public int reloadSoundTick = 15214541;
    public float fallDamageFactor = 1.0F;

    public int engineStartTime = 0;

    //Weapon variables
    /**
     * The weapon type assigned to left mouse
     */
    public EnumWeaponType primary = EnumWeaponType.NONE, secondary = EnumWeaponType.NONE;
    /**
     * Whether to alternate weapons or fire all at once
     */
    public boolean alternatePrimary = false, alternateSecondary = false;
    /**
     * Delays. Can override gun delays
     */
    public float shootDelayPrimary = -1, shootDelaySecondary = -1;
    /**
     * Damage multiplies for primary and secondary guns.
     */
    public float damageMultiplierPrimary = 1, damageMultiplierSecondary = 1;
    /**
     * Firing modes for primary and secondary guns. Minigun also an option
     */
    public EnumFireMode modePrimary = EnumFireMode.FULLAUTO, modeSecondary = EnumFireMode.FULLAUTO;
    /**
     * Sounds
     */
    public String shootSoundPrimary, shootSoundSecondary, shootReloadSound;
    /**
     * Positions of primary and secondary weapons
     */
    public ArrayList<ShootPoint> shootPointsPrimary = new ArrayList<>(), shootPointsSecondary = new ArrayList<>();
    /**
     * Pilot guns also have their own seperate array so ammo handling can be done
     */
    public ArrayList<PilotGun> pilotGuns = new ArrayList<>();
    public int reloadTimePrimary = 0,
            reloadTimeSecondary = 0;
    public String reloadSoundPrimary = "",
            reloadSoundSecondary = "";
    public int placeTimePrimary = 5,
            placeTimeSecondary = 5;
    public String placeSoundPrimary = "",
            placeSoundSecondary = "";
    //Passengers
    /**
     * The number of passengers, not including the pilot
     */
    public int numPassengers = 0;
    /**
     * Seat objects for holding information about the position and gun setup of each seat
     */
    public Seat[] seats;
    /**
     * Automatic counter used to setup ammo inventory for gunners
     */
    public int numPassengerGunners = 0;

    public float vehicleGunModelScale = 1f;

    public boolean filterAmmunition = false;

    public boolean worksUnderWater = false;

    public class ShootParticle {
        public ShootParticle(String s, float x1, float y1, float z1) {
            x = x1;
            y = y1;
            z = z1;
            name = s;
        }

        float x, y, z;
        String name;
    }

    public ArrayList<ShootParticle> shootParticlesPrimary = new ArrayList<>();
    public ArrayList<ShootParticle> shootParticlesSecondary = new ArrayList<>();

    //Inventory + Pilot guns
    /**
     * Inventory sizes
     */
    public int numCargoSlots, numBombSlots, numMissileSlots;
    /**
     * The fuel tank size
     */
    public int fuelTankSize = 100;

    //Rendering variables
    /**
     * The yOffset of the model. Shouldn't be needed if you made your model properly
     */
    public float yOffset = 10F / 16F;
    /**
     * Third person render distance
     */
    public float cameraDistance = 5F;

    //Particle system
    /**
     * A list of ambient particle emitters on this vehicle
     */
    public ArrayList<ParticleEmitter> emitters = new ArrayList<>();

    //Movement variables
    /**
     * Generic movement modifiers, no longer repeated for plane and vehicle
     */
    public float maxThrottle = 1F, maxNegativeThrottle = 0F;
    public float clutchBrake = 0F;
    /**
     * The origin of the tank turret
     */
    public Vector3f turretOrigin = new Vector3f();
    public Vector3f turretOriginOffset = new Vector3f();

    /**
     * Wheel positions
     */
    public DriveablePosition[] wheelPositions = new DriveablePosition[0];
    /**
     * Strength of springs connecting car to wheels
     */
    public float wheelSpringStrength = 0.5F;
    /**
     * The wheel radius for onGround checks
     */
    public float wheelStepHeight = 1.0F;
    /**
     * Whether or not the vehicle rolls
     */
    public boolean canRoll = true;

    /**
     * Collision points for block based collisions
     */
    public ArrayList<DriveablePosition> collisionPoints = new ArrayList<>();

    /**
     * Coefficient of drag
     */
    public float drag = 1F;

    //Boat Stuff
    /**
     * If true, then the vehicles wheels float on water
     */
    public boolean floatOnWater = false;
    /**
     * Defines where you can place this vehicle
     */
    public boolean placeableOnLand = true, placeableOnWater = false, placeableOnSponge = false;
    /**
     * The upwards force to apply to the vehicle per wheel when on water
     */
    public float buoyancy = 0.0165F;

    public float floatOffset = 0;

    /**
     * The radius within which to check for bullets
     */
    public float bulletDetectionRadius = -1F;

    /**
     * Plane is shown on ICBM Radar and engaged by AA Guns
     */
    public boolean onRadar = false;

    /**
     * Track animation frames
     */
    public int animFrames = 2;

    /**
     * Sounds
     */
    public int startSoundRange = 50;
    public String startSound = "";
    public int startSoundLength;
    public int engineSoundRange = 50;
    public String engineSound = "";
    public int engineSoundLength;
    public int backSoundRange = 50;
	public String exitSound = "";
    public int exitSoundLength = 50;
    public String idleSound = "";
    public int idleSoundLength = 50;
    public String backSound = "";
    public int backSoundLength;

    public boolean collisionDamageEnable = false;
    public boolean pushOnCollision = true;
    public float collisionDamageThrottle = 0;
    public float collisionDamageTimes = 0;

    public boolean enableReloadTime = false;

    public boolean canMountEntity = false;

    public float bulletSpread = 0F;
    public float bulletSpeed = 3F;
    public boolean rangingGun = false;

    public boolean isExplosionWhenDestroyed = false;
    //allows control over death explosion
    public float deathFireRadius = 0F;
    public float deathExplosionRadius = 4F;
    public float deathExplosionPower = 1F;
    public boolean deathExplosionBreaksBlocks = false;
    public float deathExplosionDamageVsLiving  = 1.0F;
    public float deathExplosionDamageVsPlayer  = 1.0F;
    public float deathExplosionDamageVsPlane   = 1.0F;
    public float deathExplosionDamageVsVehicle = 1.0F;
 //
    public String lockedOnSound = "";
    public int soundTime = 0;
    public int canLockOnAngle = 10;
    public int lockOnSoundTime = 60;
    public String lockOnSound = "";
    public int maxRangeLockOn = 500;
    public int lockedOnSoundRange = 5;
    public String lockingOnSound = "";

    public boolean lockOnToPlanes = false, lockOnToVehicles = false, lockOnToMechas = false, lockOnToPlayers = false, lockOnToLivings = false;

    //flares
    public boolean hasFlare = false;
    public int flareDelay = 20 * 10;
    public String flareSound = "";
    public int timeFlareUsing = 1;

    /**
     * Barrel Recoil stuff
     */
    public float recoilDist = 5F;
    public float recoilTime = 5F;

    /**
     * more nonsense
     */
    public boolean fixedPrimaryFire = false;
    public Vector3f primaryFireAngle = new Vector3f(0, 0, 0);

    public boolean fixedSecondaryFire = false;
    public Vector3f secondaryFireAngle = new Vector3f(0, 0, 0);
    /**
     * backwards compatibility attempt
     */
    public float gunLength = 0;


    public boolean setPlayerInvisible = false;

    public float maxThrottleInWater = 0.5F;
    public int maxDepth = 3;

    public ArrayList<Vector3f> leftTrackPoints = new ArrayList<>();
    public ArrayList<Vector3f> rightTrackPoints = new ArrayList<>();
    public float trackLinkLength = 0;

    /**
     * activator boolean for IT-1 reloads
     */
    public boolean IT1 = false;
    
    public static ArrayList<DriveableType> types = new ArrayList<>();

    public ArrayList<CollisionShapeBox> collisionBox = new ArrayList<>();
    public boolean fancyCollision = false;

    public CollisionShapeBox colbox;

    public DriveableType(TypeFile file) {
        super(file);
    }

    @Override
    public void preRead(TypeFile file) {
        super.preRead(file);

        types.add(this);
    }

    @Override
    public void postRead(TypeFile file) {
        super.postRead(file);

        if (bulletDetectionRadius == -1F) {
            for (CollisionBox box : health.values()) {
                bulletDetectionRadius = Math.max(bulletDetectionRadius, box.getRootPosition().length()+ box.getRadius());
            }

            bulletDetectionRadius += 1;
        }
    }

    @Override
    protected void read(ConfigMap config, TypeFile file) {

        super.read(config, file);

        try {
            //Old Pre-Read stuff
            if (config.containsKey("Passengers")) {
                numPassengers = Integer.parseInt(config.get("Passengers"));
                seats = new Seat[numPassengers + 1];

                if (config.containsKey("Passenger")) {
                    for (String entry : config.getAll("Passenger")) {
                        String[] split = ("Passenger " + entry).split(" ");
                        Seat seat = new Seat(split);
                        if (seat.id < seats.length) {
                            seats[seat.id] = seat;
                            if (seat.gunType != null) {
                                seat.gunnerID = numPassengerGunners++;
                                driveableRecipe.add(new ItemStack(seat.gunType.item));
                            }
                        }
                    }
                }
            }


            if (config.containsKey("NumWheels")) {
                wheelPositions = new DriveablePosition[Integer.parseInt(config.get("NumWheels"))];
            }

            if (config.containsKey("Driver")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "Driver");
                if (split.length > 4)
                    seats[0] = new Seat(Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                            Integer.parseInt(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]),
                            Float.parseFloat(split[6]), Float.parseFloat(split[7]));
                else
                    seats[0] = new Seat(Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                            Integer.parseInt(split[3]));
            }
            if (config.containsKey("Pilot")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "Pilot");
                if (split.length > 4)
                    seats[0] = new Seat(Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                            Integer.parseInt(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]),
                            Float.parseFloat(split[6]), Float.parseFloat(split[7]));
                else
                    seats[0] = new Seat(Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                            Integer.parseInt(split[3]));
            }


            if (FMLCommonHandler.instance().getSide().isClient() && config.containsKey("Model"))
                model = FlansMod.proxy.loadModel(config.get("Model"), shortName, ModelDriveable.class);
            vehicleGunModelScale = ConfigUtils.configFloat(config, "VehicleGunModelScale", vehicleGunModelScale);
            reloadSoundTick = ConfigUtils.configInt(config, "VehicleGunReloadTick", reloadSoundTick);
            texture = ConfigUtils.configString(config, "Texture", texture);
            isExplosionWhenDestroyed = ConfigUtils.configBool(config, "IsExplosionWhenDestroyed", isExplosionWhenDestroyed);

            //Explosion alterations
            deathFireRadius = ConfigUtils.configFloat(config, new String[]{"DeathFireRadius", "DeathFire"}, deathFireRadius);
            deathExplosionRadius = ConfigUtils.configFloat(config, new String[]{"DeathExplosionRadius", "DeathExplosion"}, deathExplosionRadius);
            deathExplosionPower = ConfigUtils.configFloat(config, "DeathExplosionPower", deathExplosionPower);
            deathExplosionBreaksBlocks = ConfigUtils.configBool(config, "DeathExplosionBreaksBlocks", deathExplosionBreaksBlocks);
            deathExplosionDamageVsLiving = ConfigUtils.configFloat(config, "DeathExplosionDamageVsLiving", deathExplosionDamageVsLiving);
            deathExplosionDamageVsPlayer = ConfigUtils.configFloat(config, "DeathExplosionDamageVsPlayer", deathExplosionDamageVsPlayer);
            deathExplosionDamageVsPlane = ConfigUtils.configFloat(config, "DeathExplosionDamageVsPlane", deathExplosionDamageVsPlane);
            deathExplosionDamageVsVehicle = ConfigUtils.configFloat(config, "DeathExplosionDamageVsVehicle", deathExplosionDamageVsVehicle);

            fallDamageFactor = ConfigUtils.configFloat(config, "FallDamageFactor", fallDamageFactor);

            //Movement Variables
            maxThrottle = ConfigUtils.configFloat(config, "MaxThrottle", maxThrottle);
            maxNegativeThrottle = ConfigUtils.configFloat(config, "MaxNegativeThrottle", maxNegativeThrottle);
            clutchBrake = ConfigUtils.configFloat(config, "ClutchBrake", clutchBrake);
            maxThrottleInWater = ConfigUtils.configFloat(config, "MaxThrottleInWater", maxThrottleInWater);
            maxDepth = ConfigUtils.configInt(config, "MaxDepth", maxDepth);
            drag = ConfigUtils.configFloat(config, "Drag", drag);

            if (config.containsKey("TurretOrigin")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "TurretOrigin");
                turretOrigin = new Vector3f(Float.parseFloat(split[1]) / 16F, Float.parseFloat(split[2]) / 16F, Float.parseFloat(split[3]) / 16F);
            }
            if (config.containsKey("TurretOriginOffset")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "TurretOriginOffset");
                turretOriginOffset = new Vector3f(Float.parseFloat(split[1]) / 16F, Float.parseFloat(split[2]) / 16F, Float.parseFloat(split[3]) / 16F);
            }
            if (config.containsKey("CollisionPoint")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "CollisionPoint");
                collisionPoints.add(new DriveablePosition(split));
            }
            if (config.containsKey("AddCollisionPoint")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddCollisionPoint");
                collisionPoints.add(new DriveablePosition(split));
            }

            collisionDamageEnable = ConfigUtils.configBool(config, "CollisionDamageEnable", collisionDamageEnable);
            collisionDamageThrottle = ConfigUtils.configFloat(config, "CollisionDamageThrottle", collisionDamageThrottle);
            collisionDamageTimes = ConfigUtils.configFloat(config, "CollisionDamageTimes", collisionDamageTimes);
            canLockOnAngle = ConfigUtils.configInt(config, "CanLockAngle", canLockOnAngle);
            lockOnSoundTime = ConfigUtils.configInt(config, "LockOnSoundTime", lockOnSoundTime);
            if (config.containsKey("LockOnToDriveables"))
                lockOnToPlanes = lockOnToVehicles = lockOnToMechas = Boolean.parseBoolean(config.get("LockOnToDriveables").toLowerCase());
            lockOnToVehicles = ConfigUtils.configBool(config, "LockOnToVehicles", lockOnToVehicles);
            lockOnToPlanes = ConfigUtils.configBool(config, "LockOnToPlanes", lockOnToPlanes);
            lockOnToMechas = ConfigUtils.configBool(config, "LockOnToMechas", lockOnToMechas);
            lockOnToPlayers = ConfigUtils.configBool(config, "LockOnToPlayers", lockOnToPlayers);
            lockOnToLivings = ConfigUtils.configBool(config, "LockOnToLivings", lockOnToLivings);
            lockedOnSoundRange = ConfigUtils.configInt(config, "LockedOnSoundRange", lockedOnSoundRange);
            canRoll = ConfigUtils.configBool(config, "CanRoll", canRoll);

            //Flares
            hasFlare = ConfigUtils.configBool(config, "HasFlare", hasFlare);
            if (config.containsKey("FlareDelay")) {
                flareDelay = Integer.parseInt(config.get("FlareDelay"));
                if (flareDelay <= 0)
                    flareDelay = 1;
            }
            if (config.containsKey("TimeFlareUsing")) {
                timeFlareUsing = Integer.parseInt(config.get("TimeFlareUsing"));
                if (timeFlareUsing <= 0)
                    timeFlareUsing = 1;
            }


            //Boats
            if (config.containsKey("Boat")) {
                placeableOnLand = false;
                placeableOnWater = true;
                floatOnWater = true;
                wheelStepHeight = 0F;
            }
            placeableOnLand = ConfigUtils.configBool(config, "PlaceableOnLand", placeableOnLand);
            placeableOnWater = ConfigUtils.configBool(config, "PlaceableOnWater", placeableOnWater);
            worksUnderWater = ConfigUtils.configBool(config, "WorksUnderwater", worksUnderWater);
            placeableOnSponge = ConfigUtils.configBool(config, "PlaceableOnSponge", placeableOnSponge);
            floatOnWater = ConfigUtils.configBool(config, "FloatOnWater", floatOnWater);
            buoyancy = ConfigUtils.configFloat(config, "Buoyancy", buoyancy);
            floatOffset = ConfigUtils.configFloat(config, "FloatOffset", floatOffset);
            canMountEntity = ConfigUtils.configBool(config, "CanMountEntity", canMountEntity);

            //Wheels
            if (config.containsKey("Wheel")) {
                for (String wheelPos : config.getAll("Wheel")) {
                    String[] split = ("Wheel " + wheelPos).split(" ");
                    int wheelIndex = Integer.parseInt(split[1]);
                    float x = Float.parseFloat(split[2]) / 16F;
                    float y = Float.parseFloat(split[3]) / 16F;
                    float z = Float.parseFloat(split[4]) / 16F;

                    EnumDriveablePart part = EnumDriveablePart.coreWheel;
                    if (split.length > 5) {
                        part = EnumDriveablePart.getPart(split[5]);
                    }

                    DriveablePosition wheelPosition = new DriveablePosition(new Vector3f(x, y, z), part);
                    wheelPositions[wheelIndex] = wheelPosition;
                }
            }

            //Wheels
            if (config.containsKey("WheelPosition")) {
                for (String wheelPos : config.getAll("WheelPosition")) {
                    String[] split = ("WheelPosition " + wheelPos).split(" ");
                    int wheelIndex = Integer.parseInt(split[1]);
                    float x = Float.parseFloat(split[2]) / 16F;
                    float y = Float.parseFloat(split[3]) / 16F;
                    float z = Float.parseFloat(split[4]) / 16F;

                    EnumDriveablePart part = EnumDriveablePart.coreWheel;
                    if (split.length > 5) {
                        part = EnumDriveablePart.getPart(split[5]);
                    }

                    DriveablePosition wheelPosition = new DriveablePosition(new Vector3f(x, y, z), part);
                    wheelPositions[wheelIndex] = wheelPosition;
                }
            }

            wheelStepHeight = ConfigUtils.configFloat(config, new String[]{"WheelRadius", "WheelStepHeight"}, wheelStepHeight);
            wheelSpringStrength = ConfigUtils.configFloat(config, new String[]{"WheelSpringStrength", "SpringStrength"}, wheelSpringStrength);
            animFrames = ConfigUtils.configInt(config, "TrackFrames", animFrames);

                //Harvesting
            harvestBlocks = ConfigUtils.configBool(config, "Harvester", harvestBlocks);
            collectHarvest = ConfigUtils.configBool(config, "CollectHarvest", collectHarvest);
            dropHarvest = ConfigUtils.configBool(config, "DropHarvest", dropHarvest);
            if (config.containsKey("HarvestBox")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "HarvestBox");
                harvestBoxSize = new Vector3f(split[1], shortName);
                harvestBoxPos = new Vector3f(split[2], shortName);
            }
            if (config.containsKey("HarvestMaterial")) {
                materialsHarvested.add(getMaterial(config.get("HarvestMaterial")));
            }
            if (config.containsKey("HarvestToolType")) {
                switch (config.get("HarvestToolType")) {
                    case "Axe":
                        materialsHarvested.add(Material.wood);
                        materialsHarvested.add(Material.plants);
                        materialsHarvested.add(Material.vine);
                        break;
                    case "Pickaxe":
                    case "Drill":
                        materialsHarvested.add(Material.iron);
                        materialsHarvested.add(Material.anvil);
                        materialsHarvested.add(Material.rock);
                        break;
                    case "Spade":
                    case "Shovel":
                    case "Excavator":
                        materialsHarvested.add(Material.ground);
                        materialsHarvested.add(Material.grass);
                        materialsHarvested.add(Material.sand);
                        materialsHarvested.add(Material.snow);
                        materialsHarvested.add(Material.clay);
                        break;
                    case "Hoe":
                    case "Combine":
                        materialsHarvested.add(Material.plants);
                        materialsHarvested.add(Material.leaves);
                        materialsHarvested.add(Material.vine);
                        materialsHarvested.add(Material.cactus);
                        materialsHarvested.add(Material.gourd);
                        break;
                    case "Tank":
                        materialsHarvested.add(Material.leaves);
                        materialsHarvested.add(Material.cactus);
                        materialsHarvested.add(Material.wood);
                        materialsHarvested.add(Material.plants);
                        break;
                }
            }

            //Cargo / Payload
            numCargoSlots = ConfigUtils.configInt(config, "CargoSlots", numCargoSlots);
            numCargoSlots = ConfigUtils.configInt(config, new String[]{"BombSlots", "MineSlots"}, numCargoSlots);
            numCargoSlots = ConfigUtils.configInt(config, "CargoSlots", numCargoSlots);
            numCargoSlots = ConfigUtils.configInt(config, new String[]{"MissileSlots", "ShellSlots"}, numCargoSlots);
            numCargoSlots = ConfigUtils.configInt(config, "CargoSlots", numCargoSlots);
            fuelTankSize = ConfigUtils.configInt(config, "FuelTankSize", fuelTankSize);
            engineStartTime = ConfigUtils.configInt(config, "EngineStartTime", engineStartTime);
            filterAmmunition = ConfigUtils.configBool(config, "FilterAmmunitionInput", filterAmmunition);

            bulletDetectionRadius = ConfigUtils.configFloat(config, "BulletDetection", bulletDetectionRadius);

            //Ammo limiters
            if (config.containsKey("AddAmmo"))
                ammo.add(BulletType.getBullet(config.get("AddAmmo")));
            acceptAllAmmo = ConfigUtils.configBool(config, new String[]{"AllowAllAmmo", "AcceptAllAmmo"}, acceptAllAmmo);

                //Weaponry
            if (config.containsKey("Primary"))
                primary = EnumWeaponType.valueOf(config.get("Primary").toUpperCase());
            if (config.containsKey("Secondary"))
                primary = EnumWeaponType.valueOf(config.get("Secondary").toUpperCase());
            shootDelayPrimary = ConfigUtils.configFloat(config, "ShootDelayPrimary", shootDelayPrimary);
            shootDelaySecondary = ConfigUtils.configFloat(config, "ShootDelaySecondary", shootDelaySecondary);
            damageMultiplierPrimary = ConfigUtils.configFloat(config, "DamageMultiplierPrimary", damageMultiplierPrimary);
            damageMultiplierSecondary = ConfigUtils.configFloat(config, "DamageMultiplierSecondary", damageMultiplierSecondary);
            if (config.containsKey("RoundsPerMinPrimary"))
                shootDelayPrimary = Float.parseFloat(config.get("RoundsPerMinPrimary")) < 1200 ? 1200F / Float.parseFloat(config.get("RoundsPerMinPrimary")) : 1;
            if (config.containsKey("RoundsPerMinSecondary"))
                shootDelaySecondary = Float.parseFloat(config.get("RoundsPerMinSecondary")) < 1200 ? 1200F / Float.parseFloat(config.get("RoundsPerMinSecondary")) : 1;
            placeTimePrimary = ConfigUtils.configInt(config, "PlaceTimePrimary", placeTimePrimary);
            placeTimeSecondary = ConfigUtils.configInt(config, "PlaceTimeSecondary", placeTimeSecondary);
            reloadTimePrimary = ConfigUtils.configInt(config, "ReloadTimePrimary", reloadTimePrimary);
            reloadTimeSecondary = ConfigUtils.configInt(config, "ReloadTimeSecondary", reloadTimeSecondary);
            alternatePrimary = ConfigUtils.configBool(config, "AlternatePrimary", alternatePrimary);
            alternateSecondary = ConfigUtils.configBool(config, "AlternateSecondary", alternateSecondary);
            if (config.containsKey("ModePrimary"))
                modePrimary = EnumFireMode.valueOf(config.get("ModePrimary").toUpperCase());
            if (config.containsKey("ModeSecondary"))
                modeSecondary = EnumFireMode.valueOf(config.get("ModeSecondary").toUpperCase());
            bulletSpeed = ConfigUtils.configFloat(config, "BulletSpeed", bulletSpeed);
            bulletSpread = ConfigUtils.configFloat(config, "BulletSpread", bulletSpread);
            rangingGun = ConfigUtils.configBool(config, "RangingGun", rangingGun);
            gunLength = ConfigUtils.configFloat(config, "GunLength", gunLength);
            recoilDist = ConfigUtils.configFloat(config, "RecoilDistance", recoilDist);
            recoilTime = ConfigUtils.configFloat(config, "RecoilTime", recoilTime);
            if (config.containsKey("ShootPointPrimary")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "ShootPointPrimary");
                DriveablePosition rootPos;
                Vector3f offPos;
                String[] gun;
                if (split.length == 9) {
                    gun = new String[]{split[0], split[1], split[2], split[3], split[4], split[5]};
                    offPos = new Vector3f(Float.parseFloat(split[6]) / 16F, Float.parseFloat(split[7]) / 16F, Float.parseFloat(split[8]) / 16F);
                } else if (split.length == 8) {
                    gun = new String[]{split[0], split[1], split[2], split[3], split[4]};
                    offPos = new Vector3f(Float.parseFloat(split[5]) / 16F, Float.parseFloat(split[6]) / 16F, Float.parseFloat(split[7]) / 16F);
                } else {
                    gun = split;
                    offPos = new Vector3f(0, 0, 0);
                }
                rootPos = getShootPoint(gun);
                ShootPoint sPoint = new ShootPoint(rootPos, offPos);
                shootPointsPrimary.add(sPoint);
                if (rootPos instanceof PilotGun)
                    pilotGuns.add((PilotGun) sPoint.rootPos);
            }
            if (config.containsKey("ShootPointSecondary")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "ShootPointSecondary");
                DriveablePosition rootPos;
                Vector3f offPos;
                String[] gun;
                if (split.length == 9) {
                    gun = new String[]{split[0], split[1], split[2], split[3], split[4], split[5]};
                    offPos = new Vector3f(Float.parseFloat(split[6]) / 16F, Float.parseFloat(split[7]) / 16F, Float.parseFloat(split[8]) / 16F);
                } else if (split.length == 8) {
                    gun = new String[]{split[0], split[1], split[2], split[3], split[4]};
                    offPos = new Vector3f(Float.parseFloat(split[5]) / 16F, Float.parseFloat(split[6]) / 16F, Float.parseFloat(split[7]) / 16F);
                } else {
                    gun = split;
                    offPos = new Vector3f(0, 0, 0);
                }
                rootPos = getShootPoint(gun);
                ShootPoint sPoint = new ShootPoint(rootPos, offPos);
                shootPointsSecondary.add(sPoint);
                if (rootPos instanceof PilotGun)
                    pilotGuns.add((PilotGun) sPoint.rootPos);
            }
            enableReloadTime = ConfigUtils.configBool(config, "EnableReloadTime", enableReloadTime);
            if (config.containsKey("ShootParticlesPrimary")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "ShootParticlesPrimary");
                shootParticlesPrimary.add(new ShootParticle(
                        split[1],
                        Float.parseFloat(split[2]),
                        Float.parseFloat(split[3]),
                        Float.parseFloat(split[4])));
            }

            if (config.containsKey("ShootParticlesSecondary")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "ShootParticlesSecondary");
                shootParticlesSecondary.add(new ShootParticle(
                        split[1],
                        Float.parseFloat(split[2]),
                        Float.parseFloat(split[3]),
                        Float.parseFloat(split[4])));
            }

            setPlayerInvisible = ConfigUtils.configBool(config, "SetPlayerInvisible", setPlayerInvisible);
            IT1 = ConfigUtils.configBool(config, "IT1", IT1);
            fixedPrimaryFire = ConfigUtils.configBool(config, "FixedPrimary", fixedPrimaryFire);
            fixedSecondaryFire = ConfigUtils.configBool(config, "FixedSecondary", fixedSecondaryFire);
            if (config.containsKey("PrimaryAngle")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "PrimaryAngle");
                primaryFireAngle = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
            }
            if (config.containsKey("SecondaryAngle")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "SecondaryAngle");
                secondaryFireAngle = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
            }


            //Backwards compatibility stuff
            if (config.containsKey("AddGun")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddGun");
                DriveablePosition rootPos;
                Vector3f offPos;
                secondary = EnumWeaponType.GUN;
                PilotGun pilotGun;

                if (split.length == 6) {
                    rootPos = (PilotGun) getShootPoint(split);
                    offPos = new Vector3f(0, 0, 0);
                    pilotGun = (PilotGun) getShootPoint(split);
                } else {
                    String[] gun = new String[]{split[0], split[1], split[2], split[3], split[4], split[5]};
                    rootPos = (PilotGun) getShootPoint(gun);
                    pilotGun = (PilotGun) getShootPoint(gun);
                    offPos = new Vector3f(Float.parseFloat(split[6]) / 16F, Float.parseFloat(split[7]) / 16F, Float.parseFloat(split[8]) / 16F);
                }
                ShootPoint sPoint = new ShootPoint(rootPos, offPos);
                shootPointsSecondary.add(sPoint);
                pilotGuns.add(pilotGun);
                driveableRecipe.add(new ItemStack(pilotGun.type.item));
            }

            if (config.containsKey("BombPosition")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "BombPosition");
                primary = EnumWeaponType.BOMB;
                if (split.length == 4)
                    shootPointsPrimary.add(new ShootPoint(new DriveablePosition(new Vector3f(Float.parseFloat(split[1]) / 16F, Float.parseFloat(split[2]) / 16F, Float.parseFloat(split[3]) / 16F), EnumDriveablePart.core), new Vector3f(0, 0, 0)));
                else if (split.length == 7)
                    shootPointsPrimary.add(new ShootPoint(new DriveablePosition(new Vector3f(Float.parseFloat(split[1]) / 16F, Float.parseFloat(split[2]) / 16F, Float.parseFloat(split[3]) / 16F), EnumDriveablePart.core), new Vector3f(Float.parseFloat(split[4]) / 16F, Float.parseFloat(split[5]) / 16F, Float.parseFloat(split[6]) / 16F)));

            }
            if (config.containsKey("BarrelPosition")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "BarrelPosition");
                primary = EnumWeaponType.SHELL;
                if (split.length == 4)
                    shootPointsPrimary.add(new ShootPoint(new DriveablePosition(new Vector3f(Float.parseFloat(split[1]) / 16F, Float.parseFloat(split[2]) / 16F, Float.parseFloat(split[3]) / 16F), EnumDriveablePart.turret), new Vector3f(0, 0, 0)));
                else if (split.length == 7)
                    shootPointsPrimary.add(new ShootPoint(new DriveablePosition(new Vector3f(Float.parseFloat(split[1]) / 16F, Float.parseFloat(split[2]) / 16F, Float.parseFloat(split[3]) / 16F), EnumDriveablePart.turret), new Vector3f(Float.parseFloat(split[4]) / 16F, Float.parseFloat(split[5]) / 16F, Float.parseFloat(split[6]) / 16F)));
            }

            shootDelaySecondary = ConfigUtils.configFloat(config, "ShootDelay", shootDelaySecondary);
            shootDelayPrimary = ConfigUtils.configFloat(config, "ShellDelay", shootDelayPrimary);

                //Recipe
            if (config.containsKey("AddRecipeParts")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddRecipeParts");
                EnumDriveablePart part = EnumDriveablePart.getPart(split[1]);
                ArrayList<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < (split.length - 2) / 2; i++) {
                    int amount = Integer.parseInt(split[2 * i + 2]);
                    boolean damaged = split[2 * i + 3].contains(".");
                    String itemName = damaged ? split[2 * i + 3].split("\\.")[0] : split[2 * i + 3];
                    int damage = damaged ? Integer.parseInt(split[2 * i + 3].split("\\.")[1]) : 0;

                    // Only add part if it is NOT null. (Seems obvious?)
                    ItemStack potentialPart = getRecipeElement(itemName, amount, damage, shortName);
                    if (potentialPart != null) {
                        stacks.add(potentialPart);
                        driveableRecipe.add(potentialPart);
                    }

                }
                ItemStack[] items = new ItemStack[stacks.size()];
                items = stacks.toArray(items);
                partwiseRecipe.put(part, items);
            }

            //Dyes
            else if (config.containsKey("AddDye")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddDye");
                int amount = Integer.parseInt(split[1]);
                int damage = -1;
                for (int i = 0; i < ItemDye.field_150923_a.length; i++) {
                    if (ItemDye.field_150923_a[i].equals(split[2]))
                        damage = i;
                }
                if (damage == -1) {
                    FlansMod.log("Failed to find dye colour : " + split[2] + " while adding " + file.name);
                    return;
                }
                driveableRecipe.add(new ItemStack(Items.dye, amount, damage));
            }


            //Health
            else if (config.containsKey("SetupPart")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "SetupPart");
                EnumDriveablePart part = EnumDriveablePart.getPart(split[1]);
                CollisionBox box;
                if (split.length > 9) {
                    box = new CollisionBox(Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]), Integer.parseInt(split[7]), Integer.parseInt(split[8]), Float.parseFloat(split[9]));
                } else {
                    box = new CollisionBox(Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]), Integer.parseInt(split[7]), Integer.parseInt(split[8]));
                }
                health.put(part, box);
            }
            if (config.containsKey("PartDeathExplosion")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "PartDeathExplosion");
                EnumDriveablePart part = EnumDriveablePart.getPart(split[1]);

                BoxExplosion exp;
                if (split.length > 5) {
                    exp = new BoxExplosion(Float.parseFloat(split[2]), Float.parseFloat(split[3]), Boolean.parseBoolean(split[4]), Float.parseFloat(split[5]), Float.parseFloat(split[6]), Float.parseFloat(split[6]), Float.parseFloat(split[7]));
                } else {
                    exp = new BoxExplosion(Float.parseFloat(split[2]), Float.parseFloat(split[3]), Boolean.parseBoolean(split[4]));
                }

                partDeathExplosions.put(part, exp);
            //Driver Position
            }
//            if (split[0].equals("DriverPart")) {
//                seats[0].part = EnumDriveablePart.getPart(split[1]);
//            }
//            else if (split[0].equals("DriverGun") || split[0].equals("PilotGun")) {
//                seats[0].gunName = split[2];
//            }
//            else if (split[0].equals("DriverGunOrigin"))
//                seats[0].gunOrigin = new Vector3f(Float.parseFloat(split[1]) / 16F, Float.parseFloat(split[2]) / 16F, Float.parseFloat(split[3]) / 16F);
//
//            else if (split[0].equals("RotatedDriverOffset")) {
//                seats[0].rotatedOffset = new Vector3f(Integer.parseInt(split[1]) / 16F, Integer.parseInt(split[2]) / 16F, Integer.parseInt(split[3]) / 16F);
//            }
//            else if (split[0].equals("RotatedPassengerOffset")) {
//                seats[Integer.parseInt(split[1])].rotatedOffset = new Vector3f(Integer.parseInt(split[2]) / 16F, Integer.parseInt(split[3]) / 16F, Integer.parseInt(split[4]) / 16F);
//            }
//            else if (split[0].equals("DriverAimSpeed")) {
//                seats[0].aimingSpeed = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
//            }
//            else if (split[0].equals("PassengerAimSpeed")) {
//                seats[Integer.parseInt(split[1])].aimingSpeed = new Vector3f(Float.parseFloat(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
//            }
//            else if (split[0].equals("DriverLegacyAiming")) {
//                seats[0].legacyAiming = Boolean.parseBoolean(split[1]);
//            }
//            else if (split[0].equals("PassengerLegacyAiming")) {
//                seats[Integer.parseInt(split[1])].legacyAiming = Boolean.parseBoolean(split[2]);
//            }
//            seats[0].yawBeforePitch = ConfigUtils.configInt(config, "DriverYawBeforePitch", seats[0].yawBeforePitch);
//            else if (split[0].equals("PassengerYawBeforePitch")) {
//                seats[Integer.parseInt(split[1])].yawBeforePitch = Boolean.parseBoolean(split[2]);
//            }
//            seats[0].latePitch = ConfigUtils.configInt(config, "DriverLatePitch", seats[0].latePitch);
//            if (split[0].equals("PassengerLatePitch")) {
//                seats[Integer.parseInt(split[1])].latePitch = Boolean.parseBoolean(split[2]);
//            }
//            else if (split[0].equals("DriverTraverseSounds")) {
//                seats[0].traverseSounds = Boolean.parseBoolean(split[1]);
//            }
//            else if (split[0].equals("PassengerTraverseSounds")) {
//                seats[Integer.parseInt(split[1])].traverseSounds = Boolean.parseBoolean(split[2]);
//            }

            if (config.containsKey("GunOrigin")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "GunOrigin");
                float x = Float.parseFloat(split[2]) / 16F;
                float y = Float.parseFloat(split[3]) / 16F;
                float z = Float.parseFloat(split[4]) / 16F;
                if (seats[Integer.parseInt(split[1])] != null)
                    seats[Integer.parseInt(split[1])].gunOrigin = new Vector3f(x, y, z);
            }

            //Y offset for badly built models :P
            yOffset = ConfigUtils.configFloat(config, "YOffset", yOffset);
            //Third person camera distance
            cameraDistance = ConfigUtils.configFloat(config, "CameraDistance", cameraDistance);

            //Sound
            startSoundRange = ConfigUtils.configInt(config, "StartSoundRange", startSoundRange);
            startSoundLength = ConfigUtils.configInt(config, "StartSoundLength", startSoundLength);
            engineSoundRange = ConfigUtils.configInt(config, "EngineSoundRange", engineSoundRange);
            engineSoundLength = ConfigUtils.configInt(config, "EngineSoundLength", engineSoundLength);
            idleSoundLength = ConfigUtils.configInt(config, "IdleSoundLength", idleSoundLength);
            exitSoundLength = ConfigUtils.configInt(config, "ExitSoundLength", exitSoundLength);
            backSoundRange = ConfigUtils.configInt(config, "BackSoundRange", backSoundRange);
            backSoundLength = ConfigUtils.configInt(config, "BackSoundLength", backSoundLength);
            soundTime = ConfigUtils.configInt(config, "SoundTime", soundTime);
            seats[0].yawSoundLength = ConfigUtils.configInt(config, "YawSoundLength", seats[0].yawSoundLength);
            seats[0].pitchSoundLength = ConfigUtils.configInt(config, "PitchSoundLength", seats[0].pitchSoundLength);

            if (config.containsKey("PassengerYawSoundLength")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "PassengerYawSoundLength");
                seats[Integer.parseInt(split[1])].yawSoundLength = Integer.parseInt(split[2]);
            }

            if (config.containsKey("PassengerPitchSoundLength")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "PassengerPitchSoundLength");
                seats[Integer.parseInt(split[1])].pitchSoundLength = Integer.parseInt(split[2]);
            }

           startSound = ConfigUtils.configDriveableSound(contentPack, config, "StartSound", startSound);
           engineSound = ConfigUtils.configDriveableSound(contentPack, config, "EngineSound", engineSound);
           idleSound = ConfigUtils.configDriveableSound(contentPack, config, "IdleSound", idleSound);
           exitSound = ConfigUtils.configDriveableSound(contentPack, config, "ExitSound", exitSound);
           backSound = ConfigUtils.configDriveableSound(contentPack, config, "BackSound", backSound);
           seats[0].yawSound = ConfigUtils.configDriveableSound(contentPack, config, "YawSound", seats[0].yawSound);
           seats[0].pitchSound = ConfigUtils.configDriveableSound(contentPack, config, "PitchSound", seats[0].pitchSound);

           if (config.containsKey("PassengerYawSound")) {
               String[] split = ConfigUtils.getSplitFromKey(config, "PassengerYawSound");
               seats[Integer.parseInt(split[1])].yawSound = split[2];
               FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
           }
           if (config.containsKey("PassengerPitchSound")) {
               String[] split = ConfigUtils.getSplitFromKey(config, "PassengerPitchSound");
               seats[Integer.parseInt(split[1])].pitchSound = split[2];
               FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
           }
           if (config.containsKey("ShootMainSound")) {
               String[] split = ConfigUtils.getSplitFromKey(config, "ShootMainSound");
               shootSoundPrimary = split[1];
               FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
           }
            if (config.containsKey("ShootSoundPrimary")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "ShootSoundPrimary");
                shootSoundPrimary = split[1];
                FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
            }
            if (config.containsKey("ShellSound")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "ShellSound");
                shootSoundPrimary = split[1];
                FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
            }
            if (config.containsKey("BombSound")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "BombSound");
                shootSoundPrimary = split[1];
                FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
            }
           if (config.containsKey("ShootReloadSound")) {
               String[] split = ConfigUtils.getSplitFromKey(config, "ShootReloadSound");
               shootReloadSound = split[1];
               FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
           }
           if (config.containsKey("ShootSecondarySound")) {
               String[] split = ConfigUtils.getSplitFromKey(config, "ShootSecondarySound");
               shootSoundSecondary = split[1];
               FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
           }
            if (config.containsKey("ShootSecondarySound")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "ShootSecondarySound");
                shootSoundSecondary = split[1];
                FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
            }

            placeSoundPrimary = ConfigUtils.configDriveableSound(contentPack, config, "PlaceSoundPrimary", placeSoundPrimary);
            placeSoundSecondary = ConfigUtils.configDriveableSound(contentPack, config, "PlaceSoundSecondary", placeSoundSecondary);
            reloadSoundPrimary = ConfigUtils.configDriveableSound(contentPack, config, "ReloadSoundPrimary", reloadSoundPrimary);
            reloadSoundSecondary = ConfigUtils.configDriveableSound(contentPack, config, "ReloadSoundSecondary", reloadSoundSecondary);
            lockedOnSound = ConfigUtils.configDriveableSound(contentPack, config, "LockedOnSound", lockedOnSound);
            lockOnSound = ConfigUtils.configGunSound(contentPack, config, "LockOnSound", lockOnSound);
            lockingOnSound = ConfigUtils.configGunSound(contentPack, config, "LockingOnSound", lockingOnSound);
            flareSound = ConfigUtils.configDriveableSound(contentPack, config, "FlareSound", flareSound);

            fancyCollision = ConfigUtils.configBool(config, "FancyCollision", fancyCollision);

            if (config.containsKey("AddCollisionMesh")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddCollisionMesh");
                CollisionShapeBox box = new CollisionShapeBox(new Vector3f(split[1], shortName), new Vector3f(split[2], shortName), new Vector3f(split[3], shortName), new Vector3f(split[4], shortName), new Vector3f(split[5], shortName), new Vector3f(split[6], shortName), new Vector3f(split[7], shortName), new Vector3f(split[8], shortName), new Vector3f(split[9], shortName), new Vector3f(split[10], shortName), "core");
                collisionBox.add(box);
                //colbox = box;
            }

            if (config.containsKey("AddCollisionMeshRaw")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddCollisionMeshRaw");
                Vector3f pos = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
                Vector3f size = new Vector3f(Float.parseFloat(split[4]), Float.parseFloat(split[5]), Float.parseFloat(split[6]));
                Vector3f p1 = new Vector3f(Float.parseFloat(split[8]), Float.parseFloat(split[9]), Float.parseFloat(split[10]));
                Vector3f p2 = new Vector3f(Float.parseFloat(split[11]), Float.parseFloat(split[12]), Float.parseFloat(split[13]));
                Vector3f p3 = new Vector3f(Float.parseFloat(split[14]), Float.parseFloat(split[15]), Float.parseFloat(split[16]));
                Vector3f p4 = new Vector3f(Float.parseFloat(split[17]), Float.parseFloat(split[18]), Float.parseFloat(split[19]));
                Vector3f p5 = new Vector3f(Float.parseFloat(split[20]), Float.parseFloat(split[21]), Float.parseFloat(split[22]));
                Vector3f p6 = new Vector3f(Float.parseFloat(split[23]), Float.parseFloat(split[24]), Float.parseFloat(split[25]));
                Vector3f p7 = new Vector3f(Float.parseFloat(split[26]), Float.parseFloat(split[27]), Float.parseFloat(split[28]));
                Vector3f p8 = new Vector3f(Float.parseFloat(split[29]), Float.parseFloat(split[30]), Float.parseFloat(split[31]));
                CollisionShapeBox box = new CollisionShapeBox(pos, size, p1, p2, p3, p4, p5, p6, p7, p8, "core");
                collisionBox.add(box);
                //colbox = box;
            }

            if (config.containsKey("AddTurretCollisionMesh")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddTurretCollisionMesh");
                CollisionShapeBox box = new CollisionShapeBox(new Vector3f(split[1], shortName), new Vector3f(split[2], shortName), new Vector3f(split[3], shortName), new Vector3f(split[4], shortName), new Vector3f(split[5], shortName), new Vector3f(split[6], shortName), new Vector3f(split[7], shortName), new Vector3f(split[8], shortName), new Vector3f(split[9], shortName), new Vector3f(split[10], shortName), "turret");
                collisionBox.add(box);
                //colbox = box;
            }

            if (config.containsKey("AddTurretCollisionMeshRaw")) {
                String[] split = ConfigUtils.getSplitFromKey(config, "AddTurretCollisionMeshRaw");
                Vector3f pos = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
                Vector3f size = new Vector3f(Float.parseFloat(split[4]), Float.parseFloat(split[5]), Float.parseFloat(split[6]));
                Vector3f p1 = new Vector3f(Float.parseFloat(split[8]), Float.parseFloat(split[9]), Float.parseFloat(split[10]));
                Vector3f p2 = new Vector3f(Float.parseFloat(split[11]), Float.parseFloat(split[12]), Float.parseFloat(split[13]));
                Vector3f p3 = new Vector3f(Float.parseFloat(split[14]), Float.parseFloat(split[15]), Float.parseFloat(split[16]));
                Vector3f p4 = new Vector3f(Float.parseFloat(split[17]), Float.parseFloat(split[18]), Float.parseFloat(split[19]));
                Vector3f p5 = new Vector3f(Float.parseFloat(split[20]), Float.parseFloat(split[21]), Float.parseFloat(split[22]));
                Vector3f p6 = new Vector3f(Float.parseFloat(split[23]), Float.parseFloat(split[24]), Float.parseFloat(split[25]));
                Vector3f p7 = new Vector3f(Float.parseFloat(split[26]), Float.parseFloat(split[27]), Float.parseFloat(split[28]));
                Vector3f p8 = new Vector3f(Float.parseFloat(split[29]), Float.parseFloat(split[30]), Float.parseFloat(split[31]));
                CollisionShapeBox box = new CollisionShapeBox(pos, size, p1, p2, p3, p4, p5, p6, p7, p8, "turret");
                collisionBox.add(box);
                //colbox = box;
            }


            if (config.containsKey("LeftLinkPoint")) {
                leftTrackPoints.add(new Vector3f(config.get("LeftLinkPoint"), shortName));
            }
            if (config.containsKey("RightLinkPoint")) {
                rightTrackPoints.add(new Vector3f(config.get("RightLinkPoint"), shortName));
            }

            trackLinkLength = ConfigUtils.configFloat(config, "TrackLinkLength", trackLinkLength);

            // ICBM Mod Radar
            onRadar = ConfigUtils.configBool(config, "OnRadar", onRadar);


            if (config.containsKey("AddParticle") || config.containsKey("AddEmitter")) {
                String[] split = ConfigUtils.getSplitFromKey(config, new String[]{"AddParticle", "AddEmitter"});
                ParticleEmitter emitter = new ParticleEmitter();
                emitter.effectType = split[1];
                emitter.emitRate = Integer.parseInt(split[2]);
                emitter.origin = new Vector3f(split[3], shortName);
                emitter.extents = new Vector3f(split[4], shortName);
                emitter.velocity = new Vector3f(split[5], shortName);
                emitter.minThrottle = Float.parseFloat(split[6]);
                emitter.maxThrottle = Float.parseFloat(split[7]);
                emitter.minHealth = Float.parseFloat(split[8]);
                emitter.maxHealth = Float.parseFloat(split[9]);
                emitter.part = split[10];
                //Scale from model coords to world coords
                emitter.origin.scale(1.0f / 16.0f);
                emitter.extents.scale(1.0f / 16.0f);
                emitter.velocity.scale(1.0f / 16.0f);
                emitters.add(emitter);
            }
        } catch (Exception e) {
            FlansMod.log("Errored reading " + file.name);
            if (FlansMod.printStackTrace) {
                e.printStackTrace();
            }
        }
    }

    private DriveablePosition getShootPoint(String[] split) {
        //Its a gun with a type
        if (split.length == 6) {
            return new PilotGun(split);
        } else if (split.length == 5) {
            return new DriveablePosition(split);
        }
        return new DriveablePosition(new Vector3f(), EnumDriveablePart.core);
    }

    public ArrayList<ShootPoint> shootPoints(boolean s) {
        return s ? shootPointsSecondary : shootPointsPrimary;
    }

    public boolean alternate(boolean s) {
        return s ? alternateSecondary : alternatePrimary;
    }

    public EnumWeaponType weaponType(boolean s) {
        return s ? secondary : primary;
    }

    public float shootDelay(boolean s) {
        return s ? shootDelaySecondary : shootDelayPrimary;
    }

    public String shootSound(boolean s) {
        return s ? shootSoundSecondary : shootSoundPrimary;
    }

    public ArrayList<ShootParticle> shootParticle(boolean s) {
        return s ? shootParticlesSecondary : shootParticlesPrimary;
    }

    public int numEngines() {
        return 1;
    }

    public int ammoSlots() {
        return numPassengerGunners + pilotGuns.size();
    }

    public boolean isValidAmmo(BulletType bulletType, EnumWeaponType weaponType) {
        return (acceptAllAmmo || ammo.contains(bulletType)) && bulletType.weaponType == weaponType;
    }

    /**
     * Find the items needed to rebuild a part. The returned array is disconnected from the template items it has looked up
     */
    public ArrayList<ItemStack> getItemsRequired(DriveablePart part, PartType engine) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        //Start with the items required to build this part
        if (partwiseRecipe.get(part.type) != null) {
            for (ItemStack stack : partwiseRecipe.get(part.type)) {
                if (stack != null) {
                    stacks.add(stack.copy());
                } else {
                    FlansMod.log("Failed to drop item on death of part [%s] on vehicle [%s]", part.type.getShortName(), name);
                }
            }
        }
        //Add the items required for the guns connected to this part
        for (PilotGun gun : pilotGuns) {
            if (gun != null && gun.part == part.type)
                stacks.add(new ItemStack(gun.type.item));
        }
        for (Seat seat : seats) {
            if (seat != null && seat.part == part.type && seat.gunType != null)
                stacks.add(new ItemStack(seat.gunType.item));
        }
        return stacks;
    }

    public static DriveableType getDriveable(String find) {
        for (DriveableType type : types) {
            if (type.shortName.equals(find))
                return type;
        }
        return null;
    }

    @Override
    public float GetRecommendedScale() {
        return 100.0f / cameraDistance;
    }

    public class ParticleEmitter {
        /**
         * The name of the effect
         */
        public String effectType;
        /**
         * The rate of emission
         */
        public int emitRate;
        /**
         * The centre of the effect emitter
         */
        public Vector3f origin;
        /**
         * The size of the box in which it emits
         */
        public Vector3f extents;
        /**
         * The velocity of the particle
         */
        public Vector3f velocity;
        /**
         * Lower throttle bound
         */
        public float minThrottle;
        /**
         * Upper throttle bound
         */
        public float maxThrottle;
        /**
         * Model part the emitter is bound to
         */
        public String part;
        /**
         * Minimum health for the emitter to work
         */
        public float minHealth;
        /**
         * Maximum health for the emitter to work
         */
        public float maxHealth;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBase GetModel() {
        return model;
    }
}
