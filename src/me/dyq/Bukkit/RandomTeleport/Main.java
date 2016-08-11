package me.dyq.Bukkit.RandomTeleport;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public static final String precreate = "[rtp]";
	public static final String postcreate = "§a[右击随机传送]";
	
	@Override
	public void onEnable()
	{
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerRightClick(PlayerInteractEvent e)
	{
		if(debug) DebugLog("block="+e.getClickedBlock().getType().toString()+" action="+e.getAction().toString());//test
		if(e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() ==Material.SIGN_POST )
		{
			Player p = e.getPlayer();
			Sign sign = (Sign)e.getClickedBlock().getState();
			if(debug) DebugLog("signstring="+sign.getLine(0)+" "+sign.getLine(1)+" "+sign.getLine(2)+" "+sign.getLine(3));//test
			if(e.getPlayer().hasPermission("rtp.use") && e.getAction() == Action.RIGHT_CLICK_BLOCK && sign.getLine(0).equals(postcreate))
			{
				try
				{
					DebugLog("do rightclick");
					int minrange = Integer.parseInt(sign.getLine(1));
					int maxrange = Integer.parseInt(sign.getLine(2));
					
					Random rand = new Random(System.currentTimeMillis());
					
					int randx = rand.nextInt(maxrange-minrange)+minrange;
					int randz = rand.nextInt(maxrange-minrange)+minrange;
					
					if(rand.nextBoolean()) randx = -randx;
					if(rand.nextBoolean()) randz = -randz;
					
					Location oldloc = p.getLocation();
					int oldx = oldloc.getBlockX();
					int oldz = oldloc.getBlockZ();
					
					int newx = oldx+randx;
					int newz = oldz+randz;
					
					World world = oldloc.getWorld();
					
					Location newloc = new Location(world,newx,64,newz);
					
					newloc.getChunk().load(true);
					int height=255;
					while(height>5)
					{
						height--;
						if(world.getBlockAt(newx, height, newz).getType() != Material.AIR)
						{
							height++;
							newloc.setY(height);
							break;
						}
					}
					world.getBlockAt(newx, height-1, newz).setType(Material.STONE);
					if(height<255) world.getBlockAt(newx, height, newz).setType(Material.AIR);
					if(height+1<255) world.getBlockAt(newx, height+1, newz).setType(Material.AIR);
					
					p.teleport(newloc);
					p.sendMessage("[随机传送] 成功传送");
					e.setCancelled(true);
				}
				catch (NumberFormatException exception)
				{
					p.sendMessage("牌子出错啦 请联系管理员");
				}
				
			}
			else if(e.getAction() == Action.LEFT_CLICK_BLOCK && p.hasPermission("rtp.admin") && sign.getLine(0).equalsIgnoreCase(precreate))
			{
				
				try
				{
					DebugLog("do leftclick min="+sign.getLine(1)+" max="+sign.getLine(2));
					int minrange = Integer.parseInt(sign.getLine(1));
					int maxrange = Integer.parseInt(sign.getLine(2));
					DebugLog("do leftclick min="+minrange+" max="+maxrange);
					if(minrange<maxrange && minrange>=0 && maxrange>0)
					{
						sign.setLine(0, postcreate);
						sign.setLine(1, Integer.toString(minrange));
						sign.setLine(2, Integer.toString(maxrange));
						sign.update();
						p.sendMessage("创建成功");
						e.setCancelled(true);
						return;
					}
				}
				catch (NumberFormatException exception)
				{
					p.sendMessage("错误: 数字不正确");
					return;
				}
			}
			
		}
		
		
	}

	private static void DebugLog(String s)
	{
		if(debug) System.out.println(s);
	}
	
	private static final boolean debug = false;
}
