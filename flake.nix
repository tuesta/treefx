{
  description = "Simple java project";
  nixConfig.bash-prompt = "[nix]λ. ";

  inputs.flake-utils.url = "github:numtide/flake-utils";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  outputs = { self, flake-utils, nixpkgs }:
    flake-utils.lib.eachDefaultSystem (system:
      let pkgs = nixpkgs.legacyPackages.${system}; in
      {
        devShells.default = with pkgs; mkShell {
          packages = [
            maven
            (pkgs.jdk21.override {enableJavaFX = true;})
            javaPackages.openjfx21
            glib
            xorg.libXxf86vm
          ];
          shellHook = ''
            export PATH="$PATH:${pkgs.maven}/bin"
            export JAVA_HOME="${pkgs.jdk21}/lib/openjdk"
            export PATH="$PATH:$JAVA_HOME/bin"
            export LD_LIBRARY_PATH="${pkgs.xorg.libXxf86vm}/lib:${pkgs.libGL}/lib:${pkgs.gtk3}/lib:${pkgs.glib.out}/lib:${pkgs.xorg.libXtst}/lib:$LD_LIBRARY_PATH"
            export JAVAFX_PATH="${pkgs.javaPackages.openjfx21}/lib"
          '';
        };
      }
    );
}
